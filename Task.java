package BankingSystem;

public class Task {
    private final String fromId, toId;
    private final int amount;
    private final long scheduledTime;
    private final String status;
    private final String taskType;
    private final int retryCount;
    private static final int MAX_RETRIES = 3;

    private Task(Builder builder) {
        this.fromId = builder.fromId;
        this.toId = builder.toId;
        this.amount = builder.amount;
        this.scheduledTime = builder.scheduledTime;
        this.status = builder.status;
        this.taskType = builder.taskType;
        this.retryCount = builder.retryCount;
    }

    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    public int getAmount() {
        return amount;
    }
    public long getScheduledTime() {
        return scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public String getTaskType() {
        return taskType;
    }

    public int getRetryCount() {
        return retryCount;
    }


    public static class Builder {
        private String fromId;
        private String toId;
        private int amount;
        private long scheduledTime;
        private String status;
        private String taskType;
        private int retryCount = 0;

        public Builder fromId(String fromId) {
            this.fromId = fromId;
            return this;
        }

        public Builder toId(String toId) {
            this.toId = toId;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder scheduledTime(long scheduledTime) {
            this.scheduledTime = scheduledTime;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder taskType(String taskType) {
            this.taskType = taskType;
            return this;
        }

        public Builder retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Task build() {
            return new Task(this);
        }


    }


}