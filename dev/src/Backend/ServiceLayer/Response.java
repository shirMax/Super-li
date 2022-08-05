package Backend.ServiceLayer;


    public class Response {
        public String errorMessage;
        public Response() {
        }
        public Response(String msg)
        {
            this.errorMessage = msg;
        }

        public boolean isErrorOccurred() {
            return errorMessage!=null;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

    }


