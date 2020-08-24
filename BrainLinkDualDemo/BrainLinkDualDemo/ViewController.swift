//
//  ViewController.swift
//  BrainLinkDualDemo
//
//  Created by macro macro on 2020/8/24.
//  Copyright © 2020 macro macro. All rights reserved.
//

import UIKit
import BrainLinkDualSDK
class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        let parse = Parse()
        //Public Method
        parse.delegate = self
        parse.setNotch(freq: 50) //default 50Hz
        parse.setNotch(enable: true) //default false
        parse.startParsing()
        parse.stopParsing()
        let bluetoothData = Data()
        parse.parsing(var1: [UInt8](bluetoothData), var2: bluetoothData.count)
    }
}

extension ViewController: ParseDelegate {
    func onRawData(rawData1: Int, rawData2: Int) {
        //Raw Data
    }
    
    func onSign(sign: Int) {
        //Sign Data
    }
    
    func parse(left: Frequency, right: Frequency) {
        //Frequency Data
    }
    
    func parse(left: EEG, right: EEG) {
        //EEG Data
    }
    
    
}
