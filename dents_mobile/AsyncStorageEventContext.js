import React, { createContext, useContext, useState } from 'react';

const AsyncStorageEventContext = createContext();

export const AsyncStorageEventProvider = ({ children }) => {
  const [updateCount, setUpdateCount] = useState(0);

  const notifyUpdate = () => {
    setUpdateCount(updateCount + 1); // Trigger a re-render in components listening to this update
  };

  return (
    <AsyncStorageEventContext.Provider value={{ notifyUpdate }}>
      {children}
    </AsyncStorageEventContext.Provider>
  );
};

export const useAsyncStorageEvent = () => useContext(AsyncStorageEventContext);
