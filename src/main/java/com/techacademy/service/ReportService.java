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


    // 日報保存
    @Transactional
    public ErrorKinds save(Report report, UserDetail userDetail) {
        Employee employee = userDetail.getEmployee();
        ErrorKinds result = loginUserRegistCheck(employee, report);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }
        report.setEmployee(employee);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    private ErrorKinds loginUserRegistCheck(Employee employee, Report report) {
        //
        List<Report> result = findByEmployeeAndReportDate(employee, report);
        if (!result.isEmpty()) {
            return ErrorKinds.DATECHECK_ERROR;
        }
        return ErrorKinds.CHECK_OK;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report report) {
        //レポートの内容をecに 今の人のコード　と　レポートの日付
        Employee employee = report.getEmployee();


        ErrorKinds result = employeeRegistCheck(employee, report);

//        Employee employee = report.getEmployee();
//        ErrorKinds result = employeeRegistCheck(employeeCheck, report);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    private ErrorKinds employeeRegistCheck(Employee employee, Report report) {
        // 画面上の入力したcode
//        String code = report.getEmployee().getCode();
        // 画面上の入力した日付
        LocalDate day = report.getReportDate();
        // 画面上の従業員の入力した日付以外取得
        List<Report> result = findByEmployeeAndReportDateNot(employee, report);
        //空ではない　→すでに登録してある日がある
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                LocalDate st = result.get(i).getReportDate();
                //今回入力とすでに登録している日があるとエラーになる
                if (day.equals(st)) {
                    return ErrorKinds.DATECHECK_ERROR;
                }
            }
        }
        return ErrorKinds.CHECK_OK;
    }

    // 日付を取得する
    public List<Report> findByEmployeeAndReportDate(Employee employee, Report report) {
        return reportRepository.findByEmployeeAndReportDate(employee, report.getReportDate());
    }

    public List<Report> findByEmployeeAndReportDateNot(Employee employee, Report report) {
        return reportRepository.findByEmployeeAndReportDateNot(employee, report.getReportDate());
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
