package org.smartregister.addo.helper;

import android.content.Context;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.InferenceRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.jeasy.rules.mvel.MVELRuleFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RulesEngineHelper {
    private Context context;
    private RulesEngine inferentialRulesEngine;
    private RulesEngine defaultRulesEngine;
    private Map<String, Rules> ruleMap;
    private final String RULE_FOLDER_PATH = "rule/";

    public RulesEngineHelper(Context context) {
        this.context = context;
        this.inferentialRulesEngine = new InferenceRulesEngine();
        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true);
        this.defaultRulesEngine = new DefaultRulesEngine(parameters);
        this.ruleMap = new HashMap<>();

    }

    private Rules getRulesFromAsset(String fileName) {
        try {
            if (!ruleMap.containsKey(fileName)) {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
                ruleMap.put(fileName, MVELRuleFactory.createRulesFrom(bufferedReader));
            }
            return ruleMap.get(fileName);
        } catch (Exception e) {
            return null;
        }
    }

    public Rules rules(String rulesFile) {
        return getRulesFromAsset(RULE_FOLDER_PATH + rulesFile);
    }
}