### 1.	Để tạo thêm field mới cho Customer, trước hết ta cần thêm column trong database:
-	Chọn database “thingsboard” ->  Schemas -> public -> Tables -> Chọn table “customer” -> Columns -> Create -> Column….
-	Các thông tin sau được tham khảo từ column “image” của table “device_profile”.
     - General:
    - name: avatar
      - Definition:
    - Data type: character varying
    - Length/Precision: 1000000
    - Collation: pg_catalog."default"
- Save
### 2.  Tạo thư mục "img" để chứa avatar mặc định: TMA_IoT_Cloud\dao\src\main\resources\img\avatar.png
Tên thư mục, tên file và đuôi file là các phần được hardcode.
### 3.	Tạo enum CUSTOMER_AVATAR_PROPERTY ở TMA_IoT_Cloud\dao\src\main\java\org\thingsboard\server\dao\model\ModelConstants.java
```java
    public static final String CUSTOMER_AVATAR_PROPERTY = "avatar";
```
### 4.	Thêm field “avatar” cho class TMA_IoT_Cloud\dao\src\main\java\org\thingsboard\server\dao\model\sql\CustomerEntity.java.
Đây là class được ánh xạ với table “customer” trong database.
-	Tạo field.
```java
    @Column(name = ModelConstants.CUSTOMER_AVATAR_PROPERTY)
    private String avatar;
```
-	Gán giá trị cho field avatar khi khởi tạo CustomerEntity.
```java

    public CustomerEntity(Customer customer) {
        if (customer.getId() != null) {
            this.setUuid(customer.getId().getId());
        }
        this.setCreatedTime(customer.getCreatedTime());
        this.tenantId = customer.getTenantId().getId();
        this.title = customer.getTitle();
        this.country = customer.getCountry();
        this.state = customer.getState();
        this.city = customer.getCity();
        this.address = customer.getAddress();
        this.address2 = customer.getAddress2();
        this.zip = customer.getZip();
        this.phone = customer.getPhone();
        this.email = customer.getEmail();
        this.additionalInfo = customer.getAdditionalInfo();
        if (customer.getExternalId() != null) {
            this.externalId = customer.getExternalId().getId();
        }
        this.avatar = customer.getAvatar();
    }        
```
-	Set avatar trong method toData(). Đây là method được sử dụng để convert từ CustomerEntity sang Customer. Customer sau đó sẽ được sử dụng để làm response khi ta gọi đến api tạo customer mới.
```java
    @Override
    public Customer toData() {
        Customer customer = new Customer(new CustomerId(this.getUuid()));
        customer.setCreatedTime(createdTime);
        customer.setTenantId(TenantId.fromUUID(tenantId));
        customer.setTitle(title);
        customer.setCountry(country);
        customer.setState(state);
        customer.setCity(city);
        customer.setAddress(address);
        customer.setAddress2(address2);
        customer.setZip(zip);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAdditionalInfo(additionalInfo);
        if (externalId != null) {
            customer.setExternalId(new CustomerId(externalId));
        }
        customer.setAvatar(avatar);
    }
```
### 5.	Thêm field “avatar” cho class TMA_IoT_Cloud\common\data\src\main\java\org\thingsboard\server\common\data\Customer.java
-	Tạo field
```java
    @Length(fieldName = "avatar", max = 1000000)
    @ApiModelProperty(position = 15, value = "Either URL or Base64 data of the avatar")
    private String avatar;
```
-	Set avatar trong constructor của Customer
```java
    public Customer(Customer customer) {
        super(customer);
        this.tenantId = customer.getTenantId();
        this.title = customer.getTitle();
        this.externalId = customer.getExternalId();
        this.avatar = customer.getAvatar();
    }
```
-	Tạo getter, setter cho avatar. Method getDefaultAvatar() được sử dụng để trả về dữ liệu avatar mặc định khi người dùng không cung cấp avatar.
```java
    public String getAvatar() {
        if (avatar == null || avatar.length() == 0) {
            return getDefaultAvatar();
        }
        return avatar;
    }

    private String getDefaultAvatar() {
        String defaultAvatar = "";
        try {
            //Get default avatar file from dao/src/main/resources/img/avatar.png
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("img/avatar.png");
            byte[] avatarBytes = IOUtils.toByteArray(is);
            //Convert avatar data to base 64 string..
            defaultAvatar = "data:image/png;base64,".concat(Base64Utils.encodeToString(avatarBytes));
        } catch (NullPointerException | IOException e) {
            throw new RuntimeException("Default avatar is not found or its size is too large");
        }
        return defaultAvatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
```
