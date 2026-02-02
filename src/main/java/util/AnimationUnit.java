package util;

public class AnimationUnit {
    private String unitClassName;
    private String framePrefix;
    private final java.util.List<Frame> frames = new java.util.ArrayList<>();

    // Getter & Setter
    public String getUnitClassName() { return unitClassName; }
    public void setUnitClassName(String unitClassName) { this.unitClassName = unitClassName; }
    public String getFramePrefix() { return framePrefix; }
    public void setFramePrefix(String framePrefix) { this.framePrefix = framePrefix; }
    public java.util.List<Frame> getFrames() { return frames; }
    public void addFrame(Frame frame) { frames.add(frame); }
    public Frame getFrame(int index) { return frames.get(index); }
    public int getFrameCount() { return frames.size(); }

}
