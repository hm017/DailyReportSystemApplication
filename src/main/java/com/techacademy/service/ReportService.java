package com.techacademy.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }
//    public List<Report> findByEmployee(UserDetail userDetail) {
//        return reportRepository.findByEmployee(Employee());
//  }



/*
    public List<Report> findAllByEmployeeCode(String code) {
        // TODO 自動生成されたメソッド・スタブ
        return reportRepository.findByEmployeeCode(code);
    }
*/
    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        String str = Integer.toString(id);
        Optional<Report> option = reportRepository.findById(str);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // ログイン中の従業員で同じ日付の日報検索
    public List<Report> findByEmployeeAndReportDate(Employee employee, LocalDate reportDate) {
        return reportRepository.findByEmployeeAndReportDate(employee, reportDate);
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report, Employee employee) {

        report.setEmployee(employee);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }













    // 日報削除
    @Transactional
    public void delete(Integer id) {
        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);
    }

}
