/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */


import React, { useState } from 'react';

import Header from './common/Header';
import Tabs from './common/Tabs';
import TaskPage from './tasks/TaskPage';
import AboutPage from './about/AboutPage';
import TrustedRootsPage from './trustedroots/TrustedRootsPage';

/**
 * Main Component of App - contains all of the app subcomponents 
 * Also handles tabs and page switching functionality 
 */
const App = () => {
  const [tabIndex, setTab] = useState(0);

  let contentView;
  switch (tabIndex) {
    case 0:
      contentView = <TaskPage />;
      break;
    case 1:
      contentView = <AboutPage />;
      break;
    case 2:
      contentView = <TrustedRootsPage />;
      break;
    default:
      contentView = <TaskPage />;
  }

  const tabs = ['Tasks', 'About', 'Trusted Roots'];

  return (
    <>
      <Header />
      {contentView}
      <Tabs tabs={tabs} selectedIndex={tabIndex} onTabChange={setTab} />
    </>
  );
};

export default App;