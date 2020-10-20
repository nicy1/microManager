package request;

public class ImageUploadRequest extends BaseRequest<byte[]> {

    public int width;

    public int height;

    public int userid;

    public String filename;

    public String fkLiveSession;
}
