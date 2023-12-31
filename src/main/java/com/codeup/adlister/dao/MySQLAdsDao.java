package com.codeup.adlister.dao;

import com.codeup.adlister.controllers.Config;
import com.codeup.adlister.models.*;
import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLAdsDao implements Ads {
    private Connection connection = null;

    public MySQLAdsDao(Config config) {
        try {
            DriverManager.registerDriver(new Driver());
            connection = DriverManager.getConnection(
                    config.getUrl(),
                    config.getUser(),
                    config.getPassword()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database!", e);
        }
    }

    @Override
    public List<Ad> all() {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM ads");
            ResultSet rs = stmt.executeQuery();
            return createAdsFromResults(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all ads.", e);
        }
    }

    @Override
    public Long insert(Ad ad) {
        try {
            String insertQuery = "INSERT INTO ads(user_id, title, description) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, ad.getUserId());
            stmt.setString(2, ad.getTitle());
            stmt.setString(3, ad.getDescription());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating a new ad.", e);
        }
    }

    private Ad extractAd(ResultSet rs) throws SQLException {
        return new Ad(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("title"),
                rs.getString("description")
        );
    }

    private List<Ad> createAdsFromResults(ResultSet rs) throws SQLException {
        List<Ad> ads = new ArrayList<>();
        while (rs.next()) {
            ads.add(extractAd(rs));
        }
        return ads;
    }

    public List<Ad> searchAds(String searchTerm) {
        try {
            String searchTitle = "SELECT DISTINCT * FROM ads WHERE title LIKE ? OR description LIKE ? ";
            String searchTermWithWildcards = "%" + searchTerm + "%";
            PreparedStatement stmt = connection.prepareStatement(searchTitle);
            stmt.setString(1, searchTermWithWildcards);
            stmt.setString(2, searchTermWithWildcards);
            ResultSet rs = stmt.executeQuery();
            return createAdsFromResults(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding matching ad", e);
        }
    }

    public void delete(long id) {
        try {
            String insertQuery = "DELETE FROM ads_categories WHERE ad_id = ?";
            PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, id);
            stmt.executeUpdate();
            insertQuery = "DELETE FROM ads WHERE id = ?";
            stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting ad" + id, e);
        }
    }

    public void update(String title, String description, long id) {
        try {
            if (title.length() > 0) {
                String insertQuery = "UPDATE ads SET title = ? WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, title);
                stmt.setLong(2, id);
                stmt.executeUpdate();
            }
            if (description.length() > 0) {
                String insertQuery = "UPDATE ads SET description = ? WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, description);
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting ad" + id, e);
        }
    }


    public Long findAds(String searchTerm) {
        try {
            String searchTitle = "SELECT * FROM ads WHERE title LIKE ? OR description LIKE ?";
            String searchTermWithWildcards = "%" + searchTerm + "%";
            PreparedStatement stmt = connection.prepareStatement(searchTitle, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, searchTermWithWildcards);
            stmt.setString(2, searchTermWithWildcards);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding matching ad", e);
        }
    }

    public List<Ad> findId(String searchTerm) {
        try {
            String searchTitle = "SELECT * FROM ads WHERE id = ?";
            String searchTermWithWildcards = searchTerm;
            PreparedStatement stmt = connection.prepareStatement(searchTitle);
            stmt.setString(1, searchTermWithWildcards);
            ResultSet rs = stmt.executeQuery();
            return createAdsFromResults(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding matching ad ID", e);
        }
    }

    public List<Ad> findAdCategories(String searchTerm) {
        try {
            String searchTitle = "SELECT * FROM ads WHERE id IN (SELECT ad_id FROM ads_categories WHERE category_id IN (SELECT id FROM categories WHERE name LIKE ?))";
            String searchTermWithWildcards = "%" + searchTerm + "%";
            PreparedStatement stmt = connection.prepareStatement(searchTitle);
            stmt.setString(1, searchTermWithWildcards);
            ResultSet rs = stmt.executeQuery();
            return createAdsFromResults(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding matching ad id", e);
        }
    }

    public List<Ad> searchAdsByUser(String searchTerm) {
        try {
            String searchTitle = "SELECT * FROM ads WHERE user_id IN (SELECT id FROM users WHERE username LIKE ?)";
            String searchTermWithWildcards = "%" + searchTerm + "%";
            PreparedStatement stmt = connection.prepareStatement(searchTitle);
            stmt.setString(1, searchTermWithWildcards);
            ResultSet rs = stmt.executeQuery();
            return createAdsFromResults(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding matching ad id", e);
        }
    }
}
