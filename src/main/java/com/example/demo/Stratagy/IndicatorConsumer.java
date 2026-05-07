package com.example.demo.Stratagy;

import com.example.demo.instrumentData.indicators.IndicatorSnapshot;

public interface IndicatorConsumer{

    void onIndicator(IndicatorSnapshot snapShot);
}
