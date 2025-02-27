package com.atsushini.hedgedocportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientForwardController {

    /**
     * フロントエンドのルートパスにリクエストをフォワードします
     * 
     * このメソッドは、静的リソース(例: .js, .css ファイル)以外の全てのリクエストを補足し
     * フロントエンドのルーティングロジックが処理できるようにルートパス("/")にフォワードします
     * 
     * @return フロントエンドのルートパスへのフォワード指示
     */
    @GetMapping("/**/{path:[^\\.]*}") // ドットを含まない全てのパス
    public String forward() {
        return "forward:/";
    }
}
