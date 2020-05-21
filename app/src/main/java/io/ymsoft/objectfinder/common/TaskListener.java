package io.ymsoft.objectfinder.common;

public interface TaskListener<T> {
    void onComplete(Task<T> task);


    class Task<T> {
        private boolean successful = false;
        private T result;
        private Throwable throwable;

        public Task() {
        }

        public Task(T result) {
            successful = true;
            this.result = result;
        }

        public Task(T result, Throwable throwable) {
            this.result = result;
            this.throwable = throwable;
        }

        public Task(T result, String errorMessage) {
            this.result = result;
            this.throwable = new Throwable(errorMessage);
        }

        public Task(boolean successful) {
            this.successful = successful;
        }

        public Task(boolean successful, String errorMessage) {
            this.successful = successful;
            this.throwable = new Throwable(errorMessage);
        }

        public Task(Throwable throwable) {
            successful = false;
            this.throwable = throwable;
        }


        public boolean isSuccessful() {
            return successful;
        }

        public void setSuccessful(boolean successful) {
            this.successful = successful;
        }

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }

        public String getMessage() {
            if(throwable != null)
                return throwable.getMessage();
            return "null";
        }
    }

}
