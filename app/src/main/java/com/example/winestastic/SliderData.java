package com.example.winestastic;

public class SliderData {
    private String imgUrl;
    private Class<?> destination;

    public enum Route{
        TASTING("TASTING", Tasting.class),
        ENTRADAS("ENTRADAS", cardDemasEntradas.class),
        HOLA("HOLA", preparaprueba.class);

        public Class<?> destinationClass;
        public String destinationName;

        Route(String destinationName, Class<?> destinationClass) {
            this.destinationClass = destinationClass;
            this.destinationName = destinationName;
        }

        public static Class<?> fromString(String destinationName){
            for(Route route: Route.values()){
                if(route.destinationName.equals(destinationName)){
                    return route.destinationClass;
                }
            }
            return null;
        }
    }

    public SliderData(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public SliderData(String imgUrl, Class<?> destination) {
        this.imgUrl = imgUrl;
        this.destination = destination;
    }

    public SliderData(String imgUrl, String destinationName) {
        this.imgUrl = imgUrl;
        this.destination = Route.fromString(destinationName);
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
