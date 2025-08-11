package com.weavecode.chatwoot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticsData {
    private String reportType;
    private String tenantId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime generatedAt;
    
    private Map<String, Object> metrics;
    private List<DataPoint> timeSeriesData;
    private List<ChartData> chartData;
    private Map<String, Object> summary;
    private List<String> insights;
    private Map<String, Object> recommendations;

    // Constructors
    public AnalyticsData() {}

    public AnalyticsData(String reportType, String tenantId) {
        this.reportType = reportType;
        this.tenantId = tenantId;
        this.generatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }

    public List<DataPoint> getTimeSeriesData() { return timeSeriesData; }
    public void setTimeSeriesData(List<DataPoint> timeSeriesData) { this.timeSeriesData = timeSeriesData; }

    public List<ChartData> getChartData() { return chartData; }
    public void setChartData(List<ChartData> chartData) { this.chartData = chartData; }

    public Map<String, Object> getSummary() { return summary; }
    public void setSummary(Map<String, Object> summary) { this.summary = summary; }

    public List<String> getInsights() { return insights; }
    public void setInsights(List<String> insights) { this.insights = insights; }

    public Map<String, Object> getRecommendations() { return recommendations; }
    public void setRecommendations(Map<String, Object> recommendations) { this.recommendations = recommendations; }

    // Utility methods
    public void addMetric(String key, Object value) {
        if (this.metrics == null) {
            this.metrics = new java.util.HashMap<>();
        }
        this.metrics.put(key, value);
    }

    public void addInsight(String insight) {
        if (this.insights == null) {
            this.insights = new java.util.ArrayList<>();
        }
        this.insights.add(insight);
    }

    public void addRecommendation(String key, Object value) {
        if (this.recommendations == null) {
            this.recommendations = new java.util.HashMap<>();
        }
        this.recommendations.put(key, value);
    }

    // Inner classes for structured data
    public static class DataPoint {
        private String label;
        private Object value;
        private LocalDateTime timestamp;
        private Map<String, Object> metadata;

        public DataPoint() {}

        public DataPoint(String label, Object value) {
            this.label = label;
            this.value = value;
        }

        // Getters and Setters
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    public static class ChartData {
        private String chartType;
        private String title;
        private List<String> labels;
        private List<Dataset> datasets;
        private Map<String, Object> options;

        public ChartData() {}

        public ChartData(String chartType, String title) {
            this.chartType = chartType;
            this.title = title;
        }

        // Getters and Setters
        public String getChartType() { return chartType; }
        public void setChartType(String chartType) { this.chartType = chartType; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public List<String> getLabels() { return labels; }
        public void setLabels(List<String> labels) { this.labels = labels; }

        public List<Dataset> getDatasets() { return datasets; }
        public void setDatasets(List<Dataset> datasets) { this.datasets = datasets; }

        public Map<String, Object> getOptions() { return options; }
        public void setOptions(Map<String, Object> options) { this.options = options; }
    }

    public static class Dataset {
        private String label;
        private List<Object> data;
        private String backgroundColor;
        private String borderColor;
        private boolean fill;
        private Map<String, Object> properties;

        public Dataset() {}

        public Dataset(String label, List<Object> data) {
            this.label = label;
            this.data = data;
        }

        // Getters and Setters
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public List<Object> getData() { return data; }
        public void setData(List<Object> data) { this.data = data; }

        public String getBackgroundColor() { return backgroundColor; }
        public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }

        public String getBorderColor() { return borderColor; }
        public void setBorderColor(String borderColor) { this.borderColor = borderColor; }

        public boolean isFill() { return fill; }
        public void setFill(boolean fill) { this.fill = fill; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }
}
