//
//  KalturaPlayerRNAnalyticsEvents.swift
//  RNKalturaPlayer
//
//  Created by Gourav.saxena on 03/11/22.
//

enum KalturaPlayerRNAnalyticsEvents: String, CaseIterable {
    
    // These events are not yet supported in iOS.
    
    case concurrencyError
    
    case bookmarkError
    
    case phoenixError
    
    case phoenixReportSent
    
    case kavaReportSent
    
    case youboraReportSent
    
    case sourceUrlSwitched
    
    case cdnSwitched
    
    case broadPeakError
}
