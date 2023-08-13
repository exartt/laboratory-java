package br.com.model;

import br.com.service.enums.JobTitleEnum;

public class ProfessionalSalary {
  private double rating;
  private String companyName;
  private JobTitleEnum jobTitle;
  private double salary;
  private int reports;
  private String location;

  public double getRating() {
    return rating;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public JobTitleEnum getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(JobTitleEnum jobTitle) {
    this.jobTitle = jobTitle;
  }

  public double getSalary() {
    return salary;
  }

  public void setSalary(double salary) {
    this.salary = salary;
  }

  public int getReports() {
    return reports;
  }

  public void setReports(int reports) {
    this.reports = reports;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
