package com.simats.optovision.models;

public class Profile {
    private int id;
    private String name;
    private int age;
    private String rightEyeVision;
    private String leftEyeVision;
    private String profileImage;

    public Profile(int id, String name, int age, String rightEyeVision, String leftEyeVision) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.rightEyeVision = rightEyeVision;
        this.leftEyeVision = leftEyeVision;
    }

    public Profile(int id, String name, int age, String rightEyeVision, String leftEyeVision, String profileImage) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.rightEyeVision = rightEyeVision;
        this.leftEyeVision = leftEyeVision;
        this.profileImage = profileImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRightEyeVision() {
        return rightEyeVision;
    }

    public void setRightEyeVision(String rightEyeVision) {
        this.rightEyeVision = rightEyeVision;
    }

    public String getLeftEyeVision() {
        return leftEyeVision;
    }

    public void setLeftEyeVision(String leftEyeVision) {
        this.leftEyeVision = leftEyeVision;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getVisionText() {
        return "R: " + rightEyeVision + "  •  L: " + leftEyeVision;
    }

    public String getAgeText() {
        return age + " years old";
    }

    public String getInitials() {
        if (name == null || name.isEmpty())
            return "U";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        } else if (parts[0].length() >= 2) {
            return parts[0].substring(0, 2).toUpperCase();
        }
        return parts[0].substring(0, 1).toUpperCase();
    }
}
