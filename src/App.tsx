1. Updated App.tsx with Authentication

import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Login from "./pages/Login";
import Index from "./pages/Index";
import Vehicle from "./pages/Vehicle";
import VehicleView from "./pages/VehicleView";
import LicenseView from "./pages/LicenseView";
import NotFound from "./pages/NotFound";
import Navbar from "./components/Navbar";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <AuthProvider>
      <TooltipProvider>
        <Toaster />
        <Sonner />
        <BrowserRouter>
          <div className="min-h-screen">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route 
                path="/" 
                element={
                  <ProtectedRoute>
                    <Index />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/vehicle" 
                element={
                  <ProtectedRoute>
                    <Navbar />
                    <Vehicle />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/vehicle/view" 
                element={
                  <ProtectedRoute>
                    <Navbar />
                    <VehicleView />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/license/view" 
                element={
                  <ProtectedRoute>
                    <Navbar />
                    <LicenseView />
                  </ProtectedRoute>
                } 
              />
              <Route path="*" element={<NotFound />} />
            </Routes>
          </div>
        </BrowserRouter>
      </TooltipProvider>
    </AuthProvider>
  </QueryClientProvider>
);

export default App;
