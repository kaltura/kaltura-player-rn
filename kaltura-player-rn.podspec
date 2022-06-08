require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "kaltura-player-rn"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "10.0", :tvos => "10.0" }
  s.source       = { :git => "https://www.kaltura.com.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift}"

  s.dependency "React-Core"
  s.dependency "KalturaPlayer"
  s.dependency "KalturaPlayer/OTT"
  s.dependency "PlayKitYoubora",:git => 'https://github.com/kaltura/playkit-ios-youbora.git', :branch => 'develop'
end
