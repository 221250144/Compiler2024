class Event implements Comparable<Event> {
    int time; // 事件发生的时间点
    boolean isStart; // 标记是否为开始事件

    // 构造方法
    public Event(int time, boolean isStart) {
        this.time = time;
        this.isStart = isStart;
    }

    // 比较方法，用于按时间排序事件
    @Override
    public int compareTo(Event other) {
        if (this.time == other.time) {
            // 如果时间相同，开始事件排在结束事件前
            return Boolean.compare(other.isStart, this.isStart);
        }
        return Integer.compare(this.time, other.time);
    }
}