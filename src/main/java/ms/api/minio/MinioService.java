package ms.api.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;

@ApplicationScoped
public class MinioService {

    private final MinioClient minioClient;

    @Inject
    public MinioService(
            @ConfigProperty(name = "minio.endpoint") String endpoint,
            @ConfigProperty(name = "minio.access-key") String accessKey,
            @ConfigProperty(name = "minio.secret-key") String secretKey
    ) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }


    public void uploadFile(String bucket, String path, String filename, String base64) throws Exception {
        String objectName = (path == null || path.isBlank()) ? filename
                : path.endsWith("/") ? path + filename
                : path + "/" + filename;

        boolean bucketExists = minioClient.bucketExists(
                io.minio.BucketExistsArgs.builder().bucket(bucket).build()
        );
        if (!bucketExists) {
            // Lanza una excepción controlada
            throw new NoSuchBucketException(bucket, objectName);
        }

        byte[] data = java.util.Base64.getDecoder().decode(base64);
        try (java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(data)) {
            minioClient.putObject(
                    io.minio.PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(bais, data.length, -1)
                            .contentType("application/octet-stream")
                            .build()
            );
        }
    }
//    public void uploadFile(String bucket, String path, String filename, String base64) throws Exception {
//        // Sanitizar y construir el objeto final (ruta + nombre)
//
//        String objectName = (path == null || path.isBlank()) ? filename
//                : path.endsWith("/") ? path + filename
//                : path + "/" + filename;
//
//        byte[] data = Base64.getDecoder().decode(base64);
//        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                            .bucket(bucket)
//                            .object(objectName)
//                            .stream(bais, data.length, -1)
//                            .contentType("application/octet-stream")
//                            .build()
//            );
//        }
//    }

    public String downloadFile(String bucket, String filename) throws Exception {
        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(filename).build());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            response.transferTo(baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }

    public List<String> listBuckets() throws Exception {
        return minioClient.listBuckets().stream().map(Bucket::name).collect(Collectors.toList());
    }



    public List<String> listObjects(String bucket) throws Exception {
        List<String> objectNames = new java.util.ArrayList<>();
        Iterable<io.minio.Result<io.minio.messages.Item>> results = minioClient.listObjects(
                io.minio.ListObjectsArgs.builder().bucket(bucket).recursive(true).build()
        );
        for (io.minio.Result<io.minio.messages.Item> result : results) {
            try {
                io.minio.messages.Item item = result.get();
                objectNames.add(item.objectName());
            } catch (Exception e) {
                // Puedes loggear o manejar el error aquí si lo deseas
                throw new RuntimeException("Error al obtener objeto de MinIO", e);
            }
        }
        return objectNames;
    }



}