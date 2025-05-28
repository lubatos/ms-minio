package ms.api.minio;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import jakarta.ws.rs.core.Response;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Path("/minio")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MinioResource {

    @Inject
    MinioService minioService;

    private static final Logger LOG = Logger.getLogger(MinioResource.class);

    @POST
    @Path("/upload")
    public Response upload(Map<String, Object> request) {
        try {
            String bucket = (String) request.get("bucket");
            String path = (String) request.get("path");
            String filename = (String) request.get("filename");
            String base64 = (String) request.get("base64");
            minioService.uploadFile(bucket, path, filename, base64);
            return Response.ok(Map.of("status", "ok")).build();
        } catch (NoSuchBucketException e) {
            // Devuelve el JSON de error
            return Response.status(400)
                    .entity(Map.of(
                            "code", "NoSuchBucket",
                            "message", "The specified bucket does not exist",
                            "bucket", e.getBucket(),
                            "object", e.getObjectName()
                    ))
                    .build();
        } catch (Exception e) {
            return Response.status(500).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @POST
    @Path("/download")
    public Map<String, Object> download(Map<String, Object> request) throws Exception {
        LOG.infof("Request /download: %s", request);
        String bucket = (String) request.get("bucket");
        String filename = (String) request.get("filename");
        String base64 = minioService.downloadFile(bucket, filename);
        Map<String, Object> response = Map.of("base64", base64);
        LOG.infof("Response /download: %s", response);
        return response;
    }

    @POST
    @Path("/list-buckets")
    public Map<String, Object> listBuckets() throws Exception {
        LOG.info("/list-buckets called");
        List<String> buckets = minioService.listBuckets();
        Map<String, Object> response = Map.of("buckets", buckets);
        LOG.infof("Response /list-buckets: %s", response);
        return response;
    }

    @POST
    @Path("/list-objects")
    public Map<String, Object> listObjects(Map<String, Object> request) throws Exception {
        LOG.infof("Request /list-objects: %s", request);
        String bucket = (String) request.get("bucket");
        List<String> objects = minioService.listObjects(bucket);
        Map<String, Object> response = Map.of("objects", objects);
        LOG.infof("Response /list-objects: %s", response);
        return response;
    }
}