/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */


import React, { useState } from 'react';

import Header from './common/Header';
import Tabs from './common/Tabs';
import TaskPage from './tasks/TaskPage';
import AboutPage from './about/AboutPage';


/**
 * Main Component of App - contains all of the app subcomponents 
 * Also handles tabs and page switching functionality 
 */
const App = () => {
  // Tab state using boolean for only 2 tabs. True - Tasks, False - About 
  const [tabIndex, setTab] = useState(true);

  return (
    <>
      <Header />
      {tabIndex ? <TaskPage /> : <AboutPage />}
      <Tabs index={tabIndex} onTabChange={setTab} />
    </>
  );
};

export default App;
