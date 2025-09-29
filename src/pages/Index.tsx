import React from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { useNavigate } from 'react-router-dom';
import { Car, FileText, Eye, User } from 'lucide-react';

const Index: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const services = [
    {
      title: 'Vehicle Registration',
      description: 'Register your new vehicle or update existing registration',
      icon: Car,
      path: '/vehicle',
      color: 'bg-blue-500'
    },
    {
      title: 'License Services',
      description: 'Apply for or renew your driving license',
      icon: FileText,
      path: '/license/view',
      color: 'bg-green-500'
    },
    {
      title: 'Vehicle Information',
      description: 'View detailed information about registered vehicles',
      icon: Eye,
      path: '/vehicle/view',
      color: 'bg-purple-500'
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="container mx-auto px-4 py-8">
        {/* Welcome Section */}
        <div className="mb-8">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle className="text-2xl">Welcome, {user?.fullName}!</CardTitle>
                  <CardDescription>
                    Access your RTO services and manage your vehicle documentation
                  </CardDescription>
                </div>
                <div className="flex items-center space-x-2">
                  <User className="h-5 w-5" />
                  <Badge variant="secondary" className="capitalize">
                    {user?.role}
                  </Badge>
                </div>
              </div>
            </CardHeader>
          </Card>
        </div>

        {/* Services Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {services.map((service, index) => {
            const IconComponent = service.icon;
            return (
              <Card key={index} className="hover:shadow-lg transition-shadow cursor-pointer">
                <CardHeader>
                  <div className="flex items-center space-x-3">
                    <div className={`p-2 rounded-lg ${service.color}`}>
                      <IconComponent className="h-6 w-6 text-white" />
                    </div>
                    <CardTitle className="text-lg">{service.title}</CardTitle>
                  </div>
                </CardHeader>
                <CardContent>
                  <CardDescription className="mb-4">
                    {service.description}
                  </CardDescription>
                  <Button 
                    onClick={() => navigate(service.path)}
                    className="w-full"
                  >
                    Access Service
                  </Button>
                </CardContent>
              </Card>
            );
          })}
        </div>

        {/* Quick Stats */}
        <div className="mt-8 grid grid-cols-1 md:grid-cols-3 gap-4">
          <Card>
            <CardContent className="p-6">
              <div className="text-center">
                <h3 className="text-2xl font-bold text-blue-600">24/7</h3>
                <p className="text-sm text-muted-foreground">Service Availability</p>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-6">
              <div className="text-center">
                <h3 className="text-2xl font-bold text-green-600">100%</h3>
                <p className="text-sm text-muted-foreground">Digital Process</p>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-6">
              <div className="text-center">
                <h3 className="text-2xl font-bold text-purple-600">Secure</h3>
                <p className="text-sm text-muted-foreground">Data Protection</p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default Index;
