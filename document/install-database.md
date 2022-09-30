### Các bước cài đặt database với dữ liêu mẫu (khi cài đặt lần đầu tiên)
1. Di chuyển đến thư mục TMA_IoT_Cloud và run câu lệnh sau để build source code: 
    - mvn clean install -DskipTests
2. Di chuyển đến thư mục TMA_IoT_Cloud\application\target\windows và run các câu lệnh sau: 
    - .\install_dev_db.bat
    - .\upgrade_dev_db.bat --fromVersion=3.3.4.1
    - .\install_dev_db.bat --loadDemo=true

### Các bước cập nhật database (khi có sự thay đổi trong file TMA_IoT_Cloud\application\src\main\data\upgrade\3.3.4.1\schema_update.sql)
1. Di chuyển đến thư mục TMA_IoT_Cloud\application và run câu lệnh sau để build lại source code:
   - mvn clean install -DskipTests
2. Di chuyển đến thư mục TMA_IoT_Cloud\application\target\windows và run câu lệnh sau:
   - .\upgrade_dev_db.bat --fromVersion=3.3.4.1
