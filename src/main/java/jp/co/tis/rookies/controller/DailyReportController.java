package jp.co.tis.rookies.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.tis.rookies.domain.DailyReport;
import jp.co.tis.rookies.service.DailyReportService;

@Controller
public class DailyReportController {
    @Autowired
    DailyReportService service;

    @ModelAttribute
    DailyReportForm setUpForm() {
        return new DailyReportForm();
    }

    /**
     * 日報入力画面.
     *
     * @param model Model
     * @return input.jsp
     */
    @RequestMapping("/daily-report/input")
    public String input(Model model) {
        return "input";
    }

    /**
     * 投稿内容確認画面.
     */
    @RequestMapping(value = "/daily-report/confirm", method = RequestMethod.POST)
    public String confirm(@Validated DailyReportForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "input";
        }

        model.addAttribute("title", form.getTitle());
        model.addAttribute("body", form.getBody());
        model.addAttribute("ss", form.getSs());
        model.addAttribute("causeOfSs", form.getCauseOfSs());
        model.addAttribute("tag", form.getTag());

        return "confirm";
    }

    /**
     * 日報入力画面に戻る.
     *
     * @return input.jsp
     */
    @RequestMapping(value = "/daily-report/back")
    public String back(Model model,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "body", required = false) String body,
            @RequestParam(value = "ss", required = false) String ss,
            @RequestParam(value = "causeOfSs", required = false) String causeOfSs,
            @RequestParam(value = "tag", required = false) String tag) {

        // TODO:値が消えないように対応.
        Map<String, String> map = new HashMap<String, String>();
        map.put("title", title);
        map.put("body", body);
        map.put("ss", ss);
        map.put("causeOfSs", causeOfSs);
        map.put("tag", tag);

        model.addAllAttributes(map);

        return "forward:/daily-report/input";
    }

    /**
     * 日報投稿処理.
     *
     * @param model Model
     * @param title タイトル
     * @param body 本文
     * @return success.jsp
     */
    @RequestMapping(value = "/daily-report/post", method = RequestMethod.POST)
    public String post(@Validated DailyReportForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "input";
        }

        DailyReport report = new DailyReport();
        BeanUtils.copyProperties(form, report);
        report.setContributor("test user"); // TODO: ユーザ管理実装後に対応
        report.setCreatedAt(new Date());

        service.create(report);

        return "success";
    }

    /**
     * 検索結果表示.
     *
     * @param word 検索単語
     * @param model Model
     * @return search.jsp
     */
    @RequestMapping(value = "/daily-report/search", method = RequestMethod.GET)
    public String search(@RequestParam(value = "word", required = false) String word, Model model) {

        List<DailyReport> list = service.search(word);

        model.addAttribute("dailyReportList", list);
        model.addAttribute("word", word);

        return "search";
    }

    /**
     * タイムライン.
     *
     * @param tag タグ
     * @param model Model
     * @return timeline.jsp
     */
    @RequestMapping(value = "/daily-report", method = RequestMethod.GET)
    public String list(@RequestParam(value = "tag", required = false) String tag, Model model) {

    	// TODO: move to service.
        List<DailyReport> list = StringUtils.isEmpty(tag) ? service.findAll() : service.filter(tag);

        model.addAttribute("dailyReportList", list);
        model.addAttribute("tag", tag);

        return "timeline";
    }
}
