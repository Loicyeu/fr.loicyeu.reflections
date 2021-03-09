package fr.loicyeu.reflections;

public enum Test {

    SLIDERQUESTION(String.class),
    UNIQUEQUESTION(Integer.class),
    MULTIPLEQUESTION(Integer.class);

    private final Class<?> clazz;

    Test(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

}
