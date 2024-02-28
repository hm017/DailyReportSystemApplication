package com.techacademy.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report, UserDetail userDetail) {
        Employee employee = userDetail.getEmployee();
        // ログイン中の従業員のデータ重複チェック
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

    //　「ログイン中の従業員 かつ 入力した日付」の日報データが存在する場合エラー
    private ErrorKinds loginUserRegistCheck(Employee employee, Report report) {
        //
        List<Report> result = reportRepository.findByEmployeeAndReportDate(employee, report.getReportDate());
        if (!result.isEmpty()) {
            return ErrorKinds.DATECHECK_ERROR;
        }
        return ErrorKinds.CHECK_OK;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report report) {

        Employee employee = report.getEmployee();
        ErrorKinds result = employeeRegistCheck(employee, report);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 「画面で表示中の従業員 かつ 入力した日付」の日報データが存在する場合エラー
    private ErrorKinds employeeRegistCheck(Employee employee, Report report) {

        // 画面上の従業員の入力した日付以外取得
        List<Report> result = reportRepository.findByEmployeeAndReportDate(employee, report.getReportDate());
        //空ではない　→すでに登録してある日がある
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                int id = result.get(i).getId();
                //今回入力とすでに登録している日があるとエラーになる
                if (id != report.getId()) {
                    return ErrorKinds.DATECHECK_ERROR;
                }
            }
        }
        return ErrorKinds.CHECK_OK;
    }

    // 日報削除
    @Transactional
    public void delete(Integer id) {
        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        String str = Integer.toString(id);
        Optional<Report> option = reportRepository.findById(str);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

}
