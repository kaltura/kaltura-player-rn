//
//  String+Dictionary.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 23/05/2022.
//

import Foundation
import PlayKit

extension String {
    
    func toDictionary() -> [String: Any]? {
        var dictionary: [String: Any]?
        if let data = self.data(using: String.Encoding.utf8) {
            do {
                dictionary = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            } catch let error as NSError {
                PKLog.debug("An error occured while creating a dictonary from the string: \(error)")
            }
        }
        return dictionary
    }
}
