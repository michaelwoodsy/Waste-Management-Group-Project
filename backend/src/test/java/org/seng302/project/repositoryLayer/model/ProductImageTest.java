@SpringBootTest
class ProductImageTest{
    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private ProductRepository productRepository;

    @Test
    void addImage(){
        Product testProduct = new Product("BEANS", "Watties Beans", "Just Beans", "Watties", 3.5, 1);
        Image image = new Image("testImage.jpg", "testImageThumbnail.jpg");
        testProduct.addImage(image);
        Assertions.assertEquals(1, testProduct.getImages.size());
    }
}