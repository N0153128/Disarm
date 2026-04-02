package dev.n0153.app.plugins.image;

import dev.n0153.app.ProcessorConfig;

public class ImageConfig implements ProcessorConfig {

    @Override
    public String getName() {
        return "Image";
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public double getVersion() {
        return 1;
    }

    @Override
    public int maxFileSizeInBytes(String mime) {
        return 5_000_000;
    }

    @Override
    public String getLogLevel() {
        return "info";
    }

    private final int logoSizeLimit; //5MB
    private final boolean keepLogo;
    private final boolean keepImage;
    private final double imgMaxWidth;
    private final double imgMaxHeight;
    private final int logoMaxWidth;
    private final int logoMaxHeight;

    private ImageConfig(Builder builder) {
        this.logoSizeLimit = builder.logoSizeLimit;
        this.keepLogo = builder.keepLogo;
        this.keepImage = builder.keepImage;
        this.imgMaxWidth = builder.imgMaxWidth;
        this.imgMaxHeight = builder.imgMaxHeight;
        this.logoMaxWidth = builder.logoMaxWidth;
        this.logoMaxHeight = builder.logoMaxHeight;
    }

    //getters
    public int getLogoSizeLimit() {
        return logoSizeLimit;
    }

    public boolean isKeepLogo() {
        return keepLogo;
    }

    public boolean isKeepImage() {
        return keepImage;
    }

    public double getImgMaxWidth() {
        return imgMaxWidth;
    }

    public double getImgMaxHeight() {
        return imgMaxHeight;
    }

    public int getLogoMaxWidth() {
        return logoMaxWidth;
    }

    public int getLogoMaxHeight() {
        return logoMaxHeight;
    }

    public static class Builder {
        private int logoSizeLimit = 5_000_000; //5MB
        private boolean keepLogo = true;
        private boolean keepImage = true;
        private double imgMaxWidth = 512;
        private double imgMaxHeight = 512;
        private int logoMaxWidth = 50;
        private int logoMaxHeight = 50;

        //setters
        public void setLogoSizeLimit(int logoSizeLimit) {
            this.logoSizeLimit = logoSizeLimit;
        }

        public void setKeepLogo(boolean keepLogo) {
            this.keepLogo = keepLogo;
        }

        public void setKeepImage(boolean keepImage) {
            this.keepImage = keepImage;
        }

        public void setImgMaxWidth(double imgMaxWidth) {
            this.imgMaxWidth = imgMaxWidth;
        }

        public void setImgMaxHeight(double imgMaxHeight) {
            this.imgMaxHeight = imgMaxHeight;
        }

        public void setLogoMaxWidth(int logoMaxWidth) {
            this.logoMaxWidth = logoMaxWidth;
        }

        public void setLogoMaxHeight(int logoMaxHeight) {
            this.logoMaxHeight = logoMaxHeight;
        }

    }
}
