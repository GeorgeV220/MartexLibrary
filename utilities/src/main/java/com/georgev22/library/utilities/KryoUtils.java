package com.georgev22.library.utilities;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import com.esotericsoftware.kryo.util.ObjectMap;
import com.georgev22.library.maps.*;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class KryoUtils {
    private static final Kryo kryo = createKryoInstance();

    public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        output.close();
        kryo.writeClassAndObject(output, object);

        return byteArrayOutputStream.toByteArray();
    }

    public static <T> T deserialize(byte[] bytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        input.close();

        return (T) kryo.readClassAndObject(input);
    }

    public static void registerClass(Class<?> clazz) {
        kryo.register(clazz, getKryo().getDefaultSerializer(clazz));
    }

    public static void registerClass(Class<?> clazz, int id) {
        kryo.register(clazz, getKryo().getDefaultSerializer(clazz), id);
    }

    public static <T> void registerClass(Class<?> clazz, Serializer<T> serializer) {
        kryo.register(clazz, serializer);
    }

    public static <T> void registerClass(Class<?> clazz, Serializer<T> serializer, int id) {
        kryo.register(clazz, serializer, id);
    }

    public static <T> void setDefaultSerializer(Class<? extends Serializer<T>> serializerClass) {
        kryo.setDefaultSerializer(serializerClass);
    }

    private static @NotNull Kryo createKryoInstance() {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        kryo.setReferenceResolver(new MapReferenceResolver());

        kryo.register(String.class);
        kryo.register(String[].class);

        kryo.register(Integer.class);
        kryo.register(Integer[].class);

        kryo.register(Double.class);
        kryo.register(Double[].class);

        kryo.register(Long.class);
        kryo.register(Long[].class);

        kryo.register(Boolean.class);
        kryo.register(Boolean[].class);

        kryo.register(BigDecimal.class);
        kryo.register(BigDecimal[].class);

        kryo.register(BigInteger.class);
        kryo.register(BigInteger[].class);

        kryo.register(Byte.class);
        kryo.register(Byte[].class);

        kryo.register(byte.class);
        kryo.register(byte[].class);

        kryo.register(Object.class);
        kryo.register(Object[].class);

        kryo.register(List.class);
        kryo.register(List[].class);


        kryo.register(Map.class);
        kryo.register(Map[].class);

        kryo.register(ObjectMap.class);
        kryo.register(ObjectMap[].class);
        kryo.register(ConcurrentObjectMap.class);
        kryo.register(ConcurrentObjectMap[].class);
        kryo.register(HashObjectMap.class);
        kryo.register(HashObjectMap[].class);
        kryo.register(LinkedObjectMap.class);
        kryo.register(LinkedObjectMap[].class);
        kryo.register(ObservableObjectMap.class);
        kryo.register(ObservableObjectMap[].class);
        kryo.register(TreeObjectMap.class);
        kryo.register(TreeObjectMap[].class);
        kryo.register(UnmodifiableObjectMap.class);
        kryo.register(UnmodifiableObjectMap[].class);

        return kryo;
    }

    public static Kryo getKryo() {
        return kryo;
    }
}
