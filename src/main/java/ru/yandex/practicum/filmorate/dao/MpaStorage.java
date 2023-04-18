package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.ArrayList;
import java.util.List;

@Service
public class MpaStorage {
    public final JdbcTemplate jdbcTemplate;
    private final List<MPA> mpaList = new ArrayList<>();

    @Autowired
    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        String sql = "SELECT * FROM Age_rating";
        mpaList.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> new MPA(rs.getInt("age_id"),
                rs.getString("name"))));
    }

    public MPA findMPAById(int id) {
        return mpaList.stream().filter(mpa -> mpa.getId() == id).findFirst().orElse(null);
    }

    public List<MPA> getMPAList() {
        return mpaList;
    }
}