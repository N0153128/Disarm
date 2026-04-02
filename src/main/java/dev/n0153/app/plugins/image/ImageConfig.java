package dev.n0153.app.plugins.image;

import dev.n0153.app.ProcessorConfig;

public class ImageConfig implements ProcessorConfig {

    @Override
    public String getName() {
        return "Image";
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
        public void setLogoSizeLimit(int newLogoSizeLimit) {
            if (newLogoSizeLimit < 0) {
                throw new IllegalArgumentException("Logo size limit cannot be less than zero");
            }
            logoSizeLimit = newLogoSizeLimit;
        }

        public void setKeepLogo(boolean keepLogo) {
            this.keepLogo = keepLogo;
        }

        public void setKeepImage(boolean keepImage) {
            this.keepImage = keepImage;
        }

        public void setImgMaxWidth(double newImgMaxWidth) {
            if (newImgMaxWidth < 0) {
                throw new IllegalArgumentException("Image max width cannot be less than zero");
            }
            imgMaxWidth = newImgMaxWidth;
        }

        public void setImgMaxHeight(double newImageMaxHeight) {
            if (newImageMaxHeight < 0) {
                throw new IllegalArgumentException("Image max height cannot be less than zero");
            }
            imgMaxHeight = newImageMaxHeight;
        }

        public void setLogoMaxWidth(int newLogoMaxWidth) {
            if (newLogoMaxWidth < 0) {
                throw new IllegalArgumentException("Logo max width cannot be less than zero");
            }
            logoMaxWidth = newLogoMaxWidth;
        }

        public void setLogoMaxHeight(int newLogoMaxHeight) {
            if (newLogoMaxHeight < 0) {
                throw new IllegalArgumentException("Logo max height cannot be less than zero");
            }
            logoMaxHeight = newLogoMaxHeight;
        }

    }
}
