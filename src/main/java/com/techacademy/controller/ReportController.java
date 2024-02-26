package com.techacademy.controller;

import java.util.List;

import javax.management.relation.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;


@Controller
@RequestMapping("reports")
public class ReportController {


    private final ReportService reportService;
    private final EmployeeService employeeService;


    @Autowired
    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {

        if (userDetail.getEmployee().getRole().toString() == "ADMIN") {
            model.addAttribute("listSize", reportService.findAll().size());
            model.addAttribute("reportList", reportService.findAll());
        } else {
            //Employee employee = userDetail.getEmployee();
            model.addAttribute("listSize", reportService.findByEmployee(userDetail.getEmployee()).size());
            model.addAttribute("reportList", reportService.findByEmployee(userDetail.getEmployee()));
        }
        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {
         model.addAttribute("report", reportService.findById(id));
         return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        model.addAttribute("name", userDetail.getEmployee().getName());
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return create(report, userDetail, model);
        }

        // 同じ日付があればエラー
        List<Report> result = reportService.findByEmployeeAndReportDate(userDetail.getEmployee(), report.getReportDate());
        if (!result.isEmpty()) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
            return create(report, userDetail, model);
        }

        reportService.save(report, userDetail.getEmployee());



        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
/*
        try {
            ErrorKinds result = employeeService.save(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(employee);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(employee);
        }
*/
        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("report", reportService.findById(id));
        return "reports/update";
    }

/*
    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@PathVariable Integer id, @Validated Report report, BindingResult res, Model model) {

        // エラーチェック
        if (res.hasErrors()) {
            model.addAttribute("report", report);
            return "reports/update";
        }

        // 画面表示中の従業員で同じ日付があればエラー
        Employee employee = report.getEmployee();
        LocalDate updateReportDate = report.getReportDate();
        List<Report> result = reportService.findByEmployeeAndReportDate(employee, updateReportDate);
        // 元々の日付
        LocalDate reportDate = reportService.findById(id).getReportDate();

        // 元の日付と同じ場合はエラーを出さない
        if (!reportDate.equals(updateReportDate)) {
            if (!result.isEmpty()) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
                return edit(report.getId(), model);
            }
        }

        reportService.update(report);

        return "redirect:/reports";
    }
*/

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id) {
        reportService.delete(id);
        return "redirect:/reports";
    }

}
