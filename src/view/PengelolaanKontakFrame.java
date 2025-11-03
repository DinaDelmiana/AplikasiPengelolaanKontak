package view;

import controller.KontakController;
import java.io.*;
import model.Kontak;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PengelolaanKontakFrame extends javax.swing.JFrame {

    private DefaultTableModel model;
    private KontakController controller;

    public PengelolaanKontakFrame() {
        initComponents();

        controller = new KontakController();
        model = new DefaultTableModel(new String[]{"No", "Nama", "Nomor Telepon", "Kategori"}, 0);  
        tblKontak.setModel(model);
        tblKontak.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblKontak.getColumnModel().getColumn(0).setMaxWidth(50);       // lebar maksimum
        tblKontak.getColumnModel().getColumn(0).setMinWidth(30);

        loadContacts();
        
        
    }

    private void loadContacts() {
    try {
        model.setRowCount(0);
        List<Kontak> contacts = controller.getAllContacts();

        for (Kontak contact : contacts) {
            model.addRow(new Object[]{
                contact.getId(), // Simpan ID asli dari database
                contact.getNama(),
                contact.getNomorTelepon(),
                contact.getKategori()
            });
        }
    } catch (SQLException e) {
        showError(e.getMessage());
    }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void addContact() {
        String nama = txtNama.getText().trim();
        String nomorTelepon = txtNomorTelepon.getText().trim();
        String kategori = (String) cmbKategori.getSelectedItem();

        if (!validatePhoneNumber(nomorTelepon)) {
            return; // Validasi nomor telepon gagal 
        }

        try {
            if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
                JOptionPane.showMessageDialog(this, "Kontak nomor telepon ini sudah ada.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.addContact(nama, nomorTelepon, kategori);
            loadContacts();
            JOptionPane.showMessageDialog(this, "Kontak berhasil ditambahkan!");
            clearInputFields();
        } catch (SQLException ex) {
            showError("Gagal menambahkan kontak: " + ex.getMessage());
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nomor telepon tidak boleh kosong.");
            return false;
        }
        
    if (!phoneNumber.matches("\\d+")) { // Hanya angka 
        JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka."); 
        return false; 
    }
    
    if (phoneNumber.length() < 8 || phoneNumber.length() > 15) { // Panjang 8-15 
        JOptionPane.showMessageDialog(this, "Nomor telepon harus memiliki panjang antara 8 hingga 15 karakter."); 
        return false; 
    } return true; 
    }
    
    private void clearInputFields() { 
        txtNama.setText(""); 
        txtNomorTelepon.setText(""); 
        cmbKategori.setSelectedIndex(0); 
    }
    
    
    // kode edit
    private void editContact() { 
    int selectedRow = tblKontak.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diperbarui.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Ambil ID asli dari tabel
    int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
    String nama = txtNama.getText().trim();
    String nomorTelepon = txtNomorTelepon.getText().trim();
    String kategori = (String) cmbKategori.getSelectedItem();

    if (!validatePhoneNumber(nomorTelepon)) {
        return;
    }

    try {
        if (controller.isDuplicatePhoneNumber(nomorTelepon, id)) {
            JOptionPane.showMessageDialog(this, "Kontak dengan nomor telepon ini sudah ada.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.updateContact(id, nama, nomorTelepon, kategori);
        loadContacts();
        JOptionPane.showMessageDialog(this, "Kontak berhasil diperbarui!");
        clearInputFields();

    } catch (SQLException ex) {
        showError("Gagal memperbarui kontak: " + ex.getMessage());
    }
    }
    
    private void populateInputFields(int selectedRow) { 
    // Ambil data dari JTable 
    String nama = model.getValueAt(selectedRow, 1).toString(); 
    String nomorTelepon = model.getValueAt(selectedRow, 2).toString(); 
    String kategori = model.getValueAt(selectedRow, 3).toString(); 

    // Set data ke komponen input 
    txtNama.setText(nama); 
    txtNomorTelepon.setText(nomorTelepon); 
    cmbKategori.setSelectedItem(kategori); 
    }
    
    
    // hapus
    private void deleteContact() { 
    int selectedRow = tblKontak.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin dihapus.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Apakah Anda yakin ingin menghapus kontak ini?",
        "Konfirmasi Hapus",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        try {
            controller.deleteContact(id);
            loadContacts();
            JOptionPane.showMessageDialog(this, "Kontak berhasil dihapus!");
            clearInputFields();
        } catch (SQLException e) {
            showError("Gagal menghapus kontak: " + e.getMessage());
        }
    }
    }
    
    // searching
    private void searchContact() { 
        String keyword = txtPencarian.getText().trim();
        if (!keyword.isEmpty()) { 
            try { 
                List<Kontak> contacts = controller.searchContacts(keyword);
                model.setRowCount(0); // Bersihkan tabel 
                for (Kontak contact : contacts) { 
                    model.addRow(new Object[]{ 
                        contact.getId(), 
                        contact.getNama(), 
                        contact.getNomorTelepon(), 
                        contact.getKategori() 
                    }); 
                } 
                if (contacts.isEmpty()) { 
                    JOptionPane.showMessageDialog(this, "Tidak ada kontak ditemukan."); 
                } 
            } catch (SQLException ex) { 
                showError(ex.getMessage()); 
            } 
        } else { 
            loadContacts(); 
        } 
    }
    
    
    // fungsi export dan import
    // ===================== FUNGSI EXPORT CSV =====================
private void exportToCSV() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan File CSV");

    int userSelection = fileChooser.showSaveDialog(this);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();

        // Tambahkan ekstensi .csv jika pengguna tidak menambahkannya
        if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
            writer.write("ID,Nama,Nomor Telepon,Kategori\n"); // Header CSV

            for (int i = 0; i < model.getRowCount(); i++) {
                writer.write(
                    model.getValueAt(i, 0) + "," +
                    model.getValueAt(i, 1) + "," +
                    model.getValueAt(i, 2) + "," +
                    model.getValueAt(i, 3) + "\n"
                );
            }

            JOptionPane.showMessageDialog(this,
                    "Data berhasil diekspor ke " + fileToSave.getAbsolutePath());

        } catch (IOException ex) {
            showError("Gagal menulis file: " + ex.getMessage());
        }
    }
}


// ===================== FUNGSI IMPORT CSV =====================
private void importFromCSV() {
    showCSVGuide();

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Apakah Anda yakin file CSV yang dipilih sudah sesuai dengan format?",
        "Konfirmasi Impor CSV",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih File CSV");

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(fileToOpen))) {
                String line = reader.readLine(); // Baca header

                if (!validateCSVHeader(line)) {
                    JOptionPane.showMessageDialog(this,
                            "Format header CSV tidak valid.\n" +
                            "Pastikan header adalah: ID,Nama,Nomor Telepon,Kategori",
                            "Kesalahan CSV", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int rowCount = 0;
                int errorCount = 0;
                int duplicateCount = 0;
                StringBuilder errorLog = new StringBuilder("Baris dengan kesalahan:\n");

                while ((line = reader.readLine()) != null) {
                    rowCount++;
                    String[] data = line.split(",");

                    if (data.length != 4) {
                        errorCount++;
                        errorLog.append("Baris ").append(rowCount + 1)
                                .append(": Format kolom tidak sesuai.\n");
                        continue;
                    }

                    String nama = data[1].trim();
                    String nomorTelepon = data[2].trim();
                    String kategori = data[3].trim();

                    if (nama.isEmpty() || nomorTelepon.isEmpty()) {
                        errorCount++;
                        errorLog.append("Baris ").append(rowCount + 1)
                                .append(": Nama atau Nomor Telepon kosong.\n");
                        continue;
                    }

                    if (!validatePhoneNumber(nomorTelepon)) {
                        errorCount++;
                        errorLog.append("Baris ").append(rowCount + 1)
                                .append(": Nomor Telepon tidak valid.\n");
                        continue;
                    }

                    try {
                        if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
                            duplicateCount++;
                            errorLog.append("Baris ").append(rowCount + 1)
                                    .append(": Kontak sudah ada.\n");
                            continue;
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(PengelolaanKontakFrame.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }

                    try {
                        controller.addContact(nama, nomorTelepon, kategori);
                    } catch (SQLException ex) {
                        errorCount++;
                        errorLog.append("Baris ").append(rowCount + 1)
                                .append(": Gagal menyimpan ke database - ")
                                .append(ex.getMessage()).append("\n");
                    }
                }

                loadContacts();

                if (errorCount > 0 || duplicateCount > 0) {
                    errorLog.append("\nTotal baris dengan kesalahan: ")
                            .append(errorCount).append("\n");
                    errorLog.append("Total baris duplikat: ")
                            .append(duplicateCount).append("\n");
                    JOptionPane.showMessageDialog(this,
                            errorLog.toString(),
                            "Kesalahan Impor",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Semua data berhasil diimpor.");
                }

            } catch (IOException ex) {
                showError("Gagal membaca file: " + ex.getMessage());
            }
        }
    }
}


// ===================== PANDUAN FORMAT CSV =====================
private void showCSVGuide() {
    String guideMessage = "Format CSV untuk impor data:\n"
            + "- Header wajib: ID,Nama,Nomor Telepon,Kategori\n"
            + "- ID dapat kosong (akan diisi otomatis)\n"
            + "- Nama dan Nomor Telepon wajib diisi\n"
            + "- Contoh isi file CSV:\n"
            + "  1, Andi, 08123456789, Teman\n"
            + "  2, Budi Doremi, 08567890123, Keluarga\n\n"
            + "Pastikan file CSV sesuai format sebelum melakukan impor.";

    JOptionPane.showMessageDialog(this, guideMessage,
            "Panduan Format CSV", JOptionPane.INFORMATION_MESSAGE);
}


// ===================== VALIDASI HEADER CSV =====================
private boolean validateCSVHeader(String header) {
    return header != null &&
           header.trim().equalsIgnoreCase("ID,Nama,Nomor Telepon,Kategori");
}


    


        @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtNomorTelepon = new javax.swing.JTextField();
        txtPencarian = new javax.swing.JTextField();
        cmbKategori = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKontak = new javax.swing.JTable();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(128, 161, 186));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 20));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("APLIKASI PENGELOLAAN KONTAK");
        jPanel1.add(jLabel1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        panel2.setBackground(new java.awt.Color(252, 249, 234));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("NAMA KONTAK");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("NOMOR TELEPON");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("KATEGORI");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("PENCARIAN");

        txtNama.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        txtNomorTelepon.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        txtPencarian.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPencarian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPencarianKeyTyped(evt);
            }
        });

        cmbKategori.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--", "Keluarga", "Teman", "Kantor" }));

        tblKontak.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblKontak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblKontak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKontakMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKontak);

        btnTambah.setBackground(new java.awt.Color(245, 203, 203));
        btnTambah.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit.setBackground(new java.awt.Color(245, 203, 203));
        btnEdit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(245, 203, 203));
        btnHapus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnExport.setBackground(new java.awt.Color(245, 203, 203));
        btnExport.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        btnImport.setBackground(new java.awt.Color(245, 203, 203));
        btnImport.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel2))
                                .addGap(18, 18, 18)
                                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtNama, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(cmbKategori, 0, 160, Short.MAX_VALUE)
                                    .addComponent(txtNomorTelepon)))
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(btnTambah)
                                .addGap(26, 26, 26)
                                .addComponent(btnEdit)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapus)))
                        .addGap(36, 36, 36)
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                .addComponent(btnExport)
                                .addGap(36, 36, 36)
                                .addComponent(btnImport)
                                .addGap(32, 32, 32))))
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 757, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNomorTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnEdit)
                    .addComponent(btnHapus))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExport)
                    .addComponent(btnImport))
                .addGap(16, 16, 16))
        );

        getContentPane().add(panel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        exportToCSV();
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        addContact();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        editContact();
    }//GEN-LAST:event_btnEditActionPerformed

    private void tblKontakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKontakMouseClicked
        int selectedRow = tblKontak.getSelectedRow(); 
        if (selectedRow != -1) { 
            populateInputFields(selectedRow); 
        }
    }//GEN-LAST:event_tblKontakMouseClicked

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteContact();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void txtPencarianKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPencarianKeyTyped
        searchContact();
    }//GEN-LAST:event_txtPencarianKeyTyped

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        importFromCSV();
    }//GEN-LAST:event_btnImportActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int konfirmasi = JOptionPane.showConfirmDialog(
        this,
        "Apakah Anda yakin ingin keluar dari aplikasi?",
        "Konfirmasi Keluar",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (konfirmasi == JOptionPane.YES_OPTION) {
        System.exit(0);
    } else {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    }
    }//GEN-LAST:event_formWindowClosing

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanKontakFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panel2;
    private javax.swing.JTable tblKontak;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNomorTelepon;
    private javax.swing.JTextField txtPencarian;
    // End of variables declaration//GEN-END:variables
}
