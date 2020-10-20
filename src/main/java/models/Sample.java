package models;

import ij.process.ImageProcessor;
import rx.Observable;
import rx.schedulers.Schedulers;
import utils.Constant;
import utils.MyUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Princewill Princewill Okoriee on 25-Jul-17.
 */
public class Sample {
    private ImageProcessor imageProcessor;
    private final String fileName;

    public Sample(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
        this.fileName = MyUtil.formatImageFileName("png", getWidth(), getHeight());
    }

    public long getHeight() {
        return imageProcessor.getHeight();
    }

    public long getWidth() {
        return imageProcessor.getWidth();
    }

    public byte[] toBytes() {
        return this.imageProcessor.getMaskArray();
    }

    public Observable<File> saveToFile() {
        return Observable.create((Observable.OnSubscribe<File>) subscriber -> {
            try {
                File f = new File(String.format("%s/%s", Constant.IMAGE_PATH, fileName));
                ImageIO.write(imageProcessor.getBufferedImage(), "png", f);
                subscriber.onNext(f);
                subscriber.onCompleted();

            } catch (IOException e) {
                subscriber.onError(e);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public java.awt.Image getImage() {
        return imageProcessor.createImage();
    }

    public java.awt.Image move(int x, int y) {
        imageProcessor.moveTo(x, y);
        return getImage();
    }

    public java.awt.Image rotateLeft() {
        imageProcessor = imageProcessor.rotateLeft();
        return getImage();
    }

    public java.awt.Image rotateRight() {
        imageProcessor = imageProcessor.rotateRight();
        return getImage();
    }

    public byte[] getImageBytes() throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageIO.write(imageProcessor.getBufferedImage(), "png", bs);
        return bs.toByteArray();
    }

    public String getFileName() {
        return fileName;
    }

    public static byte[] getImageBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bs);
        return bs.toByteArray();
    }
}
