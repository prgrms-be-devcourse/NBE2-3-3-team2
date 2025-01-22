package com.example.letmovie.domain.admin.service;

import com.example.letmovie.domain.admin.repository.AdminMovieJpaRepository;
import com.example.letmovie.domain.admin.repository.AdminScreenRepository;
import com.example.letmovie.domain.admin.repository.AdminSeatRepository;
import com.example.letmovie.domain.admin.repository.AdminShowtimeRepository;
import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.reservation.entity.Screen;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminShowtimeServiceImpl {
    @Autowired
    private AdminMovieJpaRepository adminMovieJpaRepository;

    @Autowired
    private AdminScreenRepository adminScreenRepository;

    @Autowired
    private AdminShowtimeRepository adminShowtimeRepository;

    @Autowired
    private AdminSeatRepository adminSeatRepository;

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

    // get all screens
    public List<Screen> getAllScreens() {
        return adminScreenRepository.findAll(); // 상영관 리스트 가져오기
    }

    // Get all screen names
    public List<String> getAllScreenNames() {
        return adminScreenRepository.findAll().stream()
                .map(Screen::getScreenName) // Assuming Screen has a method to get its name
                .collect(Collectors.toList());
    }

    // Get all screen names by screen ID - 상영관 오류 수정
    public Map<Long, String> getAllScreenNamesById() {
        return adminScreenRepository.findAll().stream()
                .collect(Collectors.toMap(Screen::getId, Screen::getScreenName)); // screenId로 매핑
    }

    // find all movies
    public List<Movie> findAllMovies(){
        List<Movie> movies = adminMovieJpaRepository.findAllMovies();

        return movies;
    }

    // Get all movie names
    public List<String> getAllMovieNames() {
        return adminMovieJpaRepository.findAll().stream()
                .map(Movie::getMovieName) // Assuming Movie has a method to get its title
                .collect(Collectors.toList());
    }

    // Get all movie names by movie ID - 영화명 오류 방지
    public Map<Long, String> getAllMovieNamesById() {
        return adminMovieJpaRepository.findAll().stream()
                .collect(Collectors.toMap(Movie::getId, Movie::getMovieName));
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
}
