2. Authentication Context (src/contexts/AuthContext.tsx)

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';

interface User {
  id: number;
  username: string;
  role: 'admin' | 'officer' | 'user';
  fullName: string;
  email: string;
}

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<{ success: boolean; message?: string }>;
  logout: () => void;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Mock users - replace with actual API call
const MOCK_USERS: User[] = [
  { id: 1, username: 'admin', role: 'admin', fullName: 'System Administrator', email: 'admin@rto.gov.in' },
  { id: 2, username: 'user1', role: 'user', fullName: 'John Doe', email: 'john.doe@email.com' },
  { id: 3, username: 'rto_officer', role: 'officer', fullName: 'RTO Officer', email: 'officer@rto.gov.in' }
];

const MOCK_PASSWORDS: Record<string, string> = {
  'admin': 'admin123',
  'user1': 'user123',
  'rto_officer': 'rto123'
};

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check for existing session on app load
    const token = localStorage.getItem('rto_auth_token');
    const userData = localStorage.getItem('rto_user_data');
    
    if (token && userData) {
      try {
        const parsedUser = JSON.parse(userData);
        const tokenData = JSON.parse(atob(token));
        
        // Check if token is still valid (24 hours)
        if (Date.now() < tokenData.expiresAt) {
          setUser(parsedUser);
        } else {
          // Token expired, clear storage
          localStorage.removeItem('rto_auth_token');
          localStorage.removeItem('rto_user_data');
        }
      } catch (error) {
        console.error('Error parsing stored auth data:', error);
        localStorage.removeItem('rto_auth_token');
        localStorage.removeItem('rto_user_data');
      }
    }
    
    setIsLoading(false);
  }, []);

  const login = async (username: string, password: string): Promise<{ success: boolean; message?: string }> => {
    setIsLoading(true);
    
    // Simulate API call delay
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    const foundUser = MOCK_USERS.find(u => u.username.toLowerCase() === username.toLowerCase());
    
    if (foundUser && MOCK_PASSWORDS[foundUser.username] === password) {
      const token = btoa(JSON.stringify({
        userId: foundUser.id,
        username: foundUser.username,
        timestamp: Date.now(),
        expiresAt: Date.now() + (24 * 60 * 60 * 1000) // 24 hours
      }));
      
      localStorage.setItem('rto_auth_token', token);
      localStorage.setItem('rto_user_data', JSON.stringify(foundUser));
      setUser(foundUser);
      setIsLoading(false);
      
      return { success: true };
    } else {
      setIsLoading(false);
      return { success: false, message: 'Invalid username or password' };
    }
  };

  const logout = () => {
    localStorage.removeItem('rto_auth_token');
    localStorage.removeItem('rto_user_data');
    setUser(null);
  };

  const value: AuthContextType = {
    user,
    login,
    logout,
    isAuthenticated: !!user,
    isLoading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
