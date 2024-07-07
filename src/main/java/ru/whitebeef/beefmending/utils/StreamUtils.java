package ru.whitebeef.beefmending.utils;

import java.util.stream.Stream;

public class StreamUtils {

    public static <T> Stream<? extends T> contact(Stream<? extends T>... streams) {
        Stream<T> stream = Stream.of();
        for (Stream<? extends T> secondStream : streams) {
            stream = Stream.concat(stream, secondStream);
        }
        return stream;
    }
}
