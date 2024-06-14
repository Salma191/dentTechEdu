import React,{createContext,useState} from "react";

export const AuthContext = createContext();

export const AuthProvider = ({cildren}) => {
    const [test,setTest] = useState('Test Value')
    return (
        <AuthContext.Provider value={test}>
            {children}
        </AuthContext.Provider>
    )
}