package org.example;

import java.io.IOException;
import java.util.function.Function;

public class MonadForRetrofitResult {

    public static void main(String[] args) {
        Call failingCall = new Call(true);
        Call successCall = new Call(false);
        Response response = Result.from(failingCall::execute).getOrElse(new Response("empty"));
        System.out.println(response.body);

        ResponseBody responseBody = Result
            .from(successCall::execute)
            .andThen(Response::body)
            .getOrElse(new ResponseBody("empty"));

        System.out.println(responseBody.body);

        String body = Result
            .from(failingCall::execute)
            .andThen(Response::body)
            .andThen(ResponseBody::string)
            .getOrElse("empty");

        System.out.println(body);
    }

    static class Call {

        private final boolean shouldFail;

        Call(boolean shouldFail) {
            this.shouldFail = shouldFail;
        }

        public Response execute() throws IOException {
            if (shouldFail) {
                throw new IOException("error");
            } else {
                return new Response("hello");
            }
        }
    }

    static class Response {
        private final String body;

        Response(String body) {
            this.body = body;
        }

        public ResponseBody body() {
            return new ResponseBody(body);
        }

        public ResponseBody errorBody() {
            return new ResponseBody(body);
        }
    }

    static class ResponseBody {

        private final String body;

        ResponseBody(String body) {
            this.body = body;
        }

        public String string() throws IOException {
            return body;
        }
    }

    interface Result<T> {

        <R> R fold(Function<T, R> successMapper, Function<Exception, R> failureMapper);

        static <R> Result<R> from(ExceptionalSupplier<R> supplier) {
            try {
                return new Success<>(supplier.get());
            } catch (Exception e) {
                return new Failure<>(e);
            }
        }

        default <R> Result<R> andThen(ExceptionalFunction<T, R> getter) {
            return fold(v -> from(() -> getter.get(v)), Failure::new);
        }

        default <R> Result<R> flatMap(Function<T, Result<R>> mapper) {
            return fold(mapper, Failure::new);
        }

        default T getOrElse(T empty) {
            return fold(Function.identity(), e -> empty);
        }

    }

    static class Success<T> implements Result<T> {

        private final T successValue;

        Success(T successValue) {
            this.successValue = successValue;
        }

        @Override
        public <R> R fold(Function<T, R> successMapper, Function<Exception, R> ignore) {
            return successMapper.apply(successValue);
        }
    }

    static class Failure<T> implements Result<T> {

        private final Exception exception;

        Failure(Exception exception) {
            this.exception = exception;
        }

        @Override
        public <R> R fold(Function<T, R> ignored, Function<Exception, R> failureMapper) {
            return failureMapper.apply(exception);
        }
    }

    interface ExceptionalSupplier<T> {

        T get() throws Exception;
    }

    interface ExceptionalFunction<T, R> {

        R get(T t) throws Exception;
    }

}
