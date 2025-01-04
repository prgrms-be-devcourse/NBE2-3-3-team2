package com.example.letmovie.domain.admin.service;

import com.example.letmovie.domain.admin.repository.*;
import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.movie.dto.ShowtimeDTO;
import com.example.letmovie.domain.movie.dto.TheaterDTO;
import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.movie.entity.Theater;
import com.example.letmovie.domain.payment.entity.PaymentHistory;
import com.example.letmovie.domain.reservation.dto.ScreenDTO;
import com.example.letmovie.domain.reservation.entity.Screen;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.entity.SeatType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl {

    private static final String API_KEY = "3427155cf97431b4c03ce4235b4ea90a";
    //영화 목록 (영화 이름을 검색해 영화 코드를 가져옴 movieNm -> movieCd)
    private static final String MOVIE_LIST_URL = "https://kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.xml";
    //영화 상세정보 (movieCd로 검색)
    private static final String MOVIE_INFO_URL = "https://kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.xml";

    @Autowired
    private AdminMovieJpaRepository adminMovieJpaRepository;

    @Autowired
    private AdminMemberRepository adminMemberRepository;

    @Autowired
    private AdminTheaterRepository adminTheaterRepository;

    @Autowired
    private AdminScreenRepository adminScreenRepository;

    @Autowired
    private AdminShowtimeRepository adminShowtimeRepository;

    @Autowired
    private AdminSeatRepository adminSeatRepository;

    @Autowired
    private AdminPaymentHistoryRepository adminPaymentHistoryRepository;

    // 영화 목록에서 movieCd 검색
    public String getMovieCodeByName(String movieNm) {
        try {
            String url = MOVIE_LIST_URL + "?key=" + API_KEY + "&movieNm=" + movieNm;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // XML 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(response.getBytes("UTF-8")));

            // <movie> 태그 안의 <movieNm>과 정확히 일치하는 <movieCd> 찾기
            NodeList movieNodes = document.getElementsByTagName("movie");
            String latestMovieCd = null;
            for (int i = 0; i < movieNodes.getLength(); i++) {
                Node movieNode = movieNodes.item(i);
                NodeList childNodes = movieNode.getChildNodes();
                String currentMovieCd = null;
                String currentMovieNm = null;

                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);

                    if ("movieCd".equals(childNode.getNodeName())) {
                        currentMovieCd = childNode.getTextContent();
                    } else if ("movieNm".equals(childNode.getNodeName())) {
                        currentMovieNm = childNode.getTextContent();
                    }
                }

                // movieNm이 정확히 일치하는 경우 latestMovieCd를 갱신
                if (movieNm.equals(currentMovieNm)) {
                    latestMovieCd = currentMovieCd;
                }
            }

            return latestMovieCd; // 가장 나중에 찾은 일치하는 movieCd 반환
        } catch (Exception e) {
            System.out.println("Error(getMovieCodeByName) : " + e.getMessage());
        }
        return null;
    }


    // 영화 상세정보 조회
    public Map<String, Object> getMovieInfoByCode(String movieCd) {
        Map<String, Object> movieDetails = new LinkedHashMap<>(); // LinkedHashMap 사용
        try {
            String url = MOVIE_INFO_URL + "?key=" + API_KEY + "&movieCd=" + movieCd;

            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
            Document doc = dBuilder.parse(url);
            // 제일 첫번째 태그
            doc.getDocumentElement().normalize();
            // 파싱할 tag
            NodeList nList = doc.getElementsByTagName("movieInfo");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Element eElement = (Element) nNode;

                // 데이터를 삽입 순서대로 추가
                movieDetails.put("movieName", getTagValue("movieNm", eElement));
                movieDetails.put("movieCode", getTagValue("movieCd", eElement));
                movieDetails.put("openDate", getTagValue("openDt", eElement));

                //movieDetails.put("rating", getTagValue("audits", "audit","watchGradeNm", eElement));
                // 가장 마지막 심의 등급 정보를 가져오는 로직
                NodeList auditList = eElement.getElementsByTagName("audit");
                if (auditList.getLength() > 0) {
                    Element lastAuditElement = (Element) auditList.item(auditList.getLength() - 1); // 마지막 노드 가져오기
                    String watchGradeNm = getTagValue("watchGradeNm", lastAuditElement);
                    movieDetails.put("rating", watchGradeNm); // 가장 마지막 심의 등급 저장
                } else {
                    movieDetails.put("rating", "미등록"); // 심의 등급이 없는 경우 기본값
                }


                movieDetails.put("genreName", getTagValue("genres", "genre","genreNm", eElement));
                movieDetails.put("runtime", getTagValue("showTm", eElement));
                movieDetails.put("directorName", getTagValue("directors", "director","peopleNm", eElement));

                // 배급사 필터링 로직 추가
                NodeList companyList = eElement.getElementsByTagName("company");
                for (int i = 0; i < companyList.getLength(); i++) {
                    Element companyElement = (Element) companyList.item(i);
                    String companyPartNm = getTagValue("companyPartNm", companyElement);
                    if ("배급사".equals(companyPartNm)) { // "배급사"만 필터링
                        String companyNm = getTagValue("companyNm", companyElement);
                        movieDetails.put("companys", companyNm); // 배급사 이름 저장
                        break; // 첫 번째 배급사만 가져오고 종료
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error(getMovieInfoByCode) : " + e.getMessage());
        }
        return movieDetails;
    }

    public List<Movie> findAllMovies(){
        List<Movie> movies = adminMovieJpaRepository.findAllMovies();

        return movies;
    }

    public void addMovie(Movie movie) {
        // 기본값 설정
        movie.setId(null);
        movie.setSalesAcc("0");
        movie.setAudiAcc("0");

        // 데이터 저장
        adminMovieJpaRepository.save(movie);
    }

    // 영화 검색
    public Movie findMovieById(Long id) {
        return adminMovieJpaRepository.findById(id).orElse(null);
    }

    // 영화 수정
    public void updateMovie(Movie movie) {
        Movie existingMovie = adminMovieJpaRepository.findById(movie.getId())
                .orElseThrow(() -> new IllegalArgumentException("영화가 존재하지 않습니다. ID: " + movie.getId()));

        existingMovie.setPosterImageUrl(movie.getPosterImageUrl());
        existingMovie.setStillImageUrl(movie.getStillImageUrl());
        existingMovie.setStatus(movie.getStatus());

        adminMovieJpaRepository.save(existingMovie);
    }

    // 영화 삭제
    public void deleteMovieById(Long movieId) {
        adminMovieJpaRepository.deleteById(movieId);
    }


    // 영화관 목록 조회
    public List<Theater> findAllTheaters() {
        return adminTheaterRepository.findAll();
    }

    // ID로 특정 극장 조회
    public Theater findTheaterById(Long id) {
        return adminTheaterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("극장을 찾을 수 없습니다. ID: " + id));
    }

    // 극장 추가
    public void addTheater(Theater theater) {
        adminTheaterRepository.save(theater);
    }

    // 극장 수정
    /*public void updateTheater(Theater theater) {
        Theater existingTheater = adminTheaterRepository.findById(theater.getId())
                .orElseThrow(() -> new IllegalArgumentException("극장을 찾을 수 없습니다. ID: " + theater.getId()));
        existingTheater.setTheaterName(theater.getTheaterName());
        adminTheaterRepository.save(existingTheater);
    }*/
    public void updateTheater(TheaterDTO theaterDto) {
        Theater existingTheater = adminTheaterRepository.findById(theaterDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("극장을 찾을 수 없습니다. ID: " + theaterDto.getId()));
        existingTheater.setTheaterName(theaterDto.getTheaterName());
        adminTheaterRepository.save(existingTheater);
    }

    // 극장 삭제
    public void deleteTheaterById(Long id) {
        adminTheaterRepository.deleteById(id);
    }

    // 상영관 목록조회
    public List<Screen> findAllScreens() {
        return adminScreenRepository.findAll();
    }

    // ID조회
    public Screen findScreenById(Long screenId) {
        return adminScreenRepository.findById(screenId)
                .orElseThrow(() -> new IllegalArgumentException("상영관을 찾을 수 없습니다. ID: " + screenId));
    }

    // 상영관 추가
    public void addScreen(ScreenDTO screenDTO) {
        Theater theater = adminTheaterRepository.findById(screenDTO.getTheaterId())
                .orElseThrow(() -> new IllegalArgumentException("영화관을 찾을 수 없습니다. ID: " + screenDTO.getTheaterId()));

        Screen screen = Screen.builder()
                .screenName(screenDTO.getScreenName())
                .theater(theater)
                .build();

        adminScreenRepository.save(screen);
    }

    // 상영관 수정
    public void updateScreen(ScreenDTO screenDTO) {
        if (screenDTO.getId() == null) {
            throw new IllegalArgumentException("Screen ID는 필수입니다.");
        }

        Screen existingScreen = adminScreenRepository.findById(screenDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("상영관을 찾을 수 없습니다. ID: " + screenDTO.getId()));

        existingScreen.setScreenName(screenDTO.getScreenName());

        adminScreenRepository.save(existingScreen);
    }

    // 상영관 삭제
    public void deleteScreen(Long screenId) {
        if (!adminScreenRepository.existsById(screenId)) {
            throw new IllegalArgumentException("상영관을 찾을 수 없습니다. ID: " + screenId);
        }
        adminScreenRepository.deleteById(screenId);
    }

    // 좌석
    public List<Screen> getAllScreens() {
        return adminScreenRepository.findAll(); // 상영관 리스트 가져오기
    }

    // 좌석 추가 로직
    @Transactional
    public void addSeatsToScreen(Long screenId, int seatLow, int seatCol) {
        Screen screen = adminScreenRepository.findById(screenId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid screen ID"));
        for (int row = 1; row <= seatLow; row++) {
            for (int col = 1; col <= seatCol; col++) {
                if (adminSeatRepository.existsByScreenAndSeatLowAndSeatCol(screen, row, col)) {
                    continue; // 중복 방지
                }
                Seat seat = Seat.builder()
                        .screen(screen)
                        .seatLow(row)
                        .seatCol(col)
                        .seatType(SeatType.REGULAR)
                        .isAble(true)
                        .price(10000)
                        .build();
                adminSeatRepository.save(seat);
            }
        }
    }


    // 특정 상영관의 좌석 가져오기
    public List<Seat> getSeatsByScreenId(Long screenId) {
        return adminSeatRepository.findByScreenId(screenId);
    }

    public Seat getSeatById(Long seatId) {
        return adminSeatRepository.findById(seatId).orElseThrow(() -> new IllegalArgumentException("Invalid seat ID"));
    }

    // 좌석 정보 수정
    public void updateSeat(Long seatId, SeatType seatType, int price) {
        Seat seat = adminSeatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid seat ID"));
        seat.setSeatType(seatType);
        seat.setPrice(price);
        adminSeatRepository.save(seat);
    }

    @Transactional
    public void deleteAllSeatsByScreenId(Long screenId) {
        Screen screen = adminScreenRepository.findById(screenId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid screen ID"));
        adminSeatRepository.deleteByScreen(screen);
    }

    //상영시간대
    // Create a new showtime
    @Transactional
    public void createShowtime(Showtime showtime) {
        adminShowtimeRepository.save(showtime);
    }

    // Retrieve all showtimes
    public List<Showtime> getAllShowtimes() {
        return adminShowtimeRepository.findAll();
    }

    // Get all screen names
    public List<String> getAllScreenNames() {
        return adminScreenRepository.findAll().stream()
                .map(Screen::getScreenName) // Assuming Screen has a method to get its name
                .collect(Collectors.toList());
    }

    // Get all movie names
    public List<String> getAllMovieNames() {
        return adminMovieJpaRepository.findAll().stream()
                .map(Movie::getMovieName) // Assuming Movie has a method to get its title
                .collect(Collectors.toList());
    }

    // Retrieve showtimes by screen ID
    public List<Showtime> getShowtimesByScreenId(Long screenId) {
        return adminShowtimeRepository.findByScreenId(screenId);
    }

    // Retrieve showtimes by movie ID
    public List<Showtime> getShowtimesByMovieId(Long movieId) {
        return adminShowtimeRepository.findByMovieId(movieId);
    }

    public void addShowtime(Long screenId, Long movieId, LocalDate showtimeDate, LocalTime showtimeTime, int totalSeats, int remainingSeats) {
        Screen screen = adminScreenRepository.findById(screenId).orElseThrow(() -> new IllegalArgumentException("Invalid screen ID"));
        Movie movie = adminMovieJpaRepository.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Invalid movie ID"));

        Showtime showtime = Showtime.builder()
                .screen(screen)
                .movie(movie)
                .showtimeDate(showtimeDate)
                .showtimeTime(showtimeTime)
                .totalSeats(totalSeats)
                .remainingSeats(remainingSeats)
                .build();

        adminShowtimeRepository.save(showtime);
    }

    public int countAvailableSeatsByScreenId(Long screenId) {
        return adminSeatRepository.countAvailableSeatsByScreenId(screenId);
    }

    // Delete showtime
    @Transactional
    public void deleteShowtime(Long id) {
        adminShowtimeRepository.deleteById(id);
    }

    public Showtime getShowtimeById(Long id) {
        return adminShowtimeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid showtime ID"));
    }


    // 닉네임으로 회원 조회
    public List<Member> findMemberByName(String nickname) {
        return adminMemberRepository.findByNicknameContainingIgnoreCase(nickname);
    }

    // ID로 회원 조회
    public Member findMemberById(Long memberId) {
        return adminMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. ID: " + memberId));
    }

    // 회원 수정
    public void updateMember(Member member) {
        System.out.println("id : " + member.getId());
        Member existingMember = adminMemberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. ID: " + member.getId()));

        System.out.println("nickname : " + member.getNickname());
        System.out.println("nickname : " + member.getGrade());
        System.out.println("nickname : " + member.getMemberStatus());
        existingMember.setNickname(member.getNickname());

        existingMember.setGrade(member.getGrade());
        existingMember.setMemberStatus(member.getMemberStatus());

        adminMemberRepository.save(existingMember);
    }

    // 특정 회원의 결제 내역 조회
    public List<PaymentHistory> findPaymentHistoryByMemberId(Long memberId) {
        String partnerUserId = memberId.toString(); // Member의 ID를 partnerUserId와 매칭
        return adminPaymentHistoryRepository.findByPartnerUserId(partnerUserId);
        //return adminPaymentHistoryRepository.findByPaymentMemberId(memberId);
    }

    // tag값의 정보를 가져오는 함수
    public static String getTagValue(String tag, Element eElement) {

        //결과를 저장할 result 변수 선언
        String result = "";

        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();

        result = nlList.item(0).getTextContent();

        return result;
    }

    // 자식 tag값의 정보를 가져오는 함수
    public static String getTagValue(String tag, String childTag, Element eElement) {

        //결과를 저장할 result 변수 선언
        String result = "";

        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();

        for(int i = 0; i < eElement.getElementsByTagName(childTag).getLength(); i++) {

            //result += nlList.item(i).getFirstChild().getTextContent() + " ";
            result += nlList.item(i).getChildNodes().item(0).getTextContent() + " ";
        }

        return result;
    }

    // 손자 tag값의 정보를 가져오는 함수
    public static String getTagValue(String parentTag, String childTag, String grandChildTag, Element eElement) {
        // 결과를 저장할 result 변수
        StringBuilder result = new StringBuilder();

        // parentTag의 NodeList 가져오기
        NodeList parentNodeList = eElement.getElementsByTagName(parentTag);

        for (int i = 0; i < parentNodeList.getLength(); i++) {
            Node parentNode = parentNodeList.item(i);

            if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element parentElement = (Element) parentNode;

                // childTag의 NodeList 가져오기
                NodeList childNodeList = parentElement.getElementsByTagName(childTag);

                for (int j = 0; j < childNodeList.getLength(); j++) {
                    Node childNode = childNodeList.item(j);

                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) childNode;

                        // grandChildTag 값 가져오기
                        NodeList grandChildNodeList = childElement.getElementsByTagName(grandChildTag);
                        for (int k = 0; k < grandChildNodeList.getLength(); k++) {
                            Node grandChildNode = grandChildNodeList.item(k);
                            if (grandChildNode.getNodeType() == Node.ELEMENT_NODE) {
                                result.append(grandChildNode.getTextContent()).append(" ");
                            }
                        }
                    }
                }
            }
        }
        return result.toString().trim(); // 결과 반환
    }

}
