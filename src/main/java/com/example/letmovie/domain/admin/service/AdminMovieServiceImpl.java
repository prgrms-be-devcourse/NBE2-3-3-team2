package com.example.letmovie.domain.admin.service;

import com.example.letmovie.domain.admin.repository.AdminMovieJpaRepository;
import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.entity.Theater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminMovieServiceImpl {
    @Value("${movie.api.key}")
    private String API_KEY;
    //영화 목록 (영화 이름을 검색해 영화 코드를 가져옴 movieNm -> movieCd)
    private static final String MOVIE_LIST_URL = "https://kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.xml";
    //영화 상세정보 (movieCd로 검색)
    private static final String MOVIE_INFO_URL = "https://kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.xml";

    private final RestTemplate restTemplate;

    @Autowired
    private AdminMovieJpaRepository adminMovieJpaRepository;

    public AdminMovieServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    // 영화 목록에서 movieCd 검색
    public String getMovieCodeByName(String movieNm) {
        try {
            String url = MOVIE_LIST_URL + "?key=" + API_KEY + "&movieNm=" + movieNm;
            //RestTemplate restTemplate = new RestTemplate();
            System.out.println("Injected RestTemplate: " + restTemplate);
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

    public List<Movie> findMovieByName(String movieNm) {
        List<Movie> movies = adminMovieJpaRepository.findMovieByName(movieNm);
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
