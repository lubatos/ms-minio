//package ms.api.minio;
//import io.quarkus.test.junit.QuarkusTest;
////import io.restassured.http.ContentType;
//import org.junit.jupiter.api.Test;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.Map;
//
//import static sun.security.util.KnownOIDs.ContentType;
//
////import static io.restassured.RestAssured.given;
////import static org.hamcrest.Matchers.*;
//
//@QuarkusTest
//public class MinioResourceTest {
//
//    @Test
//    void testListBuckets() {
//        given()
//                .contentType(ContentType.JSON)
//                .post("/minio/list-buckets")
//                .then()
//                .statusCode(200)
//                .body("buckets", notNullValue());
//    }
//
//    @Test
//    void testUploadAndDownload() {
//        String bucket = "test-bucket";
//        String filename = "hello.txt";
//        String content = "hola mundo";
//        String base64 = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
//
//        // Upload
//        given()
//                .contentType(ContentType.JSON)
//                .body(Map.of(
//                        "bucket", bucket,
//                        "filename", filename,
//                        "base64", base64
//                ))
//                .post("/minio/upload")
//                .then()
//                .statusCode(200)
//                .body("status", equalTo("ok"));
//
//        // Download
//        given()
//                .contentType(ContentType.JSON)
//                .body(Map.of(
//                        "bucket", bucket,
//                        "filename", filename
//                ))
//                .post("/minio/download")
//                .then()
//                .statusCode(200)
//                .body("base64", equalTo(base64));
//    }
//}