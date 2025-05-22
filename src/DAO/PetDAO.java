package DAO;

import database.ConnectDB;
import model.Pet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetDAO {

    public static List<Pet> getAllPets() {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT * FROM pets";

        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pet pet = new Pet();
                pet.setId(rs.getInt("id"));
                pet.setName(rs.getString("name"));
                pet.setType(rs.getString("type"));
                pet.setPrice(rs.getDouble("price"));
                list.add(pet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Pet getPetById(int petId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
