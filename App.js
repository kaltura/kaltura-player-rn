import React from "react";
import type { Node } from "react";
import { SafeAreaView } from "react-native";

import KalturaPlayer from "./src/components/KalturaPlayer";

const App: () => Node = () => {
  return (
    <SafeAreaView>
      <KalturaPlayer />
    </SafeAreaView>
  );
};

export default App;
