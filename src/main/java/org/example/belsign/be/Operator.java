package org.example.belsign.be;

public class Operator {

        private String operatorFirstName;
        private String operatorLastName;
        private int operatorID;
        private String username;
        private String password;

        public Operator(int operatorID, String operatorFirstName, String operatorLastName) {
            this.operatorID = operatorID;
            this.operatorFirstName = operatorFirstName;
            this.operatorLastName = operatorLastName;
        }

        public String getOperatorFirstName() {
            return operatorFirstName;
        }

        public void setOperatorFirstName(String operatorFirstName) {
            this.operatorFirstName = operatorFirstName;
        }

        public String getOperatorLastName() {
            return operatorLastName;
        }

        public void setOperatorLastName(String operatorLastName) {
            this.operatorLastName = operatorLastName;
        }

        public int getOperatorID() {
            return operatorID;
        }

        public void setOperatorID(int operatorID) {
            this.operatorID = operatorID;
        }

}
