package database;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void main(String[] args) { 
        String sql = "CREATE TABLE IF NOT EXISTS contacts (" 
                + "id INTEGER PRIMARY KEY AUTOINCREMENT," 
                + "nama TEXT NOT NULL," 
                + "nomor_telepon TEXT NOT NULL," 
                + "kategori TEXT" 
                + ");"; 
        try (java.sql.Connection conn = DatabaseConnection.getConnection(); 
                java.sql.Statement stmt = conn.createStatement()) { 
            stmt.execute(sql); 
            System.out.println("Tabel 'contacts' berhasil dibuat atau sudah ada."); 
        } catch (SQLException e) { 
            System.out.println("Error: " + e.getMessage()); }
    }
}
    
