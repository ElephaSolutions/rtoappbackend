
import { useBusinessConfig } from '@/hooks/useBusinessConfig';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Car, FileText, Calendar, TrendingUp, Plus, Eye } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { act, useEffect, useState } from 'react';

interface MetadataResponse {
  totalVehicles: number;
  expiringSoon: number;
}

interface RecentActivityResponse {
  revisionType: 'ADD' | 'MOD' | 'MOD';
  vehicleNumber: string;
  timestamp: string;
}

const BACKEND_URL = "https://rtoappbyourself.onrender.com"

const Dashboard = () => {
  const { config } = useBusinessConfig();
  const [recentActivities, setRecentActivities] = useState<RecentActivityResponse[]>([])
  const [metadata, setMetadata] = useState<MetadataResponse>({totalVehicles: 0, expiringSoon: 0})
  const navigate = useNavigate()

  useEffect(
    () => {
      fetch(`${BACKEND_URL}/api/v1/vehicle/recent-activity`) 
      .then(response => {
        if(response.status === 401 || response.status === 403)
          navigate("/login")
        if(!response.ok)
          throw new Error("Error fetching recent activities from backend")
        return response.json()
      })
      .then(responseJson => {
        setRecentActivities(responseJson)
      })

      fetch(`${BACKEND_URL}/api/v1/vehicle/metadata`)
      .then(response => {
        if(!response.ok)
          throw new Error("Error fetching recent activities from backend")
        return response.json()
      })
      .then(responseJson => {
        setMetadata(responseJson)
      })
    }, []
  )

  const getActivityTimeString = (activity: RecentActivityResponse): string => {
    const timeSinceActivityOccuredInMilli = Math.abs(new Date() - new Date(activity.timestamp))
    const timeSinceActivityOccuredInSeconds = timeSinceActivityOccuredInMilli / 1000
    const timeSinceActivityOccuredInMinutes = timeSinceActivityOccuredInSeconds / 60
    const timeSinceActivityOccuredInHours = timeSinceActivityOccuredInMinutes / 60
    if (timeSinceActivityOccuredInHours > 1)
      return `${Math.ceil(timeSinceActivityOccuredInHours)} hours ago`
    else if (timeSinceActivityOccuredInMinutes > 1)
      return `${Math.ceil(timeSinceActivityOccuredInMinutes)} minutes ago`
    else 
      return `${Math.ceil(timeSinceActivityOccuredInSeconds)} seconds ago`
  }

  const stats = [
    {
      title: 'Total Vehicles',
      // value: '24',
      // change: '+2 this month',
      icon: Car,
      color: 'bg-blue-500'
    },
    {
      title: 'Expiring Soon',
      // value: '6',
      // change: 'Next 30 days',
      icon: Calendar,
      color: 'bg-orange-500'
    },
    // {
    //   title: 'Documents',
    //   value: '144',
    //   change: 'All types',
    //   icon: FileText,
    //   color: 'bg-green-500'
    // },
    // {
    //   title: 'This Month',
    //   value: '8',
    //   change: '+33% from last month',
    //   icon: TrendingUp,
    //   color: 'bg-purple-500'
    // }
  ];

  const quickActions = [
    {
      title: 'Add New Vehicle',
      description: 'Register a new vehicle with all required documents',
      icon: Plus,
      href: '/vehicle',
      color: config?.theme.primary
    },
    {
      title: 'View All Vehicles',
      description: 'Browse and manage existing vehicle records',
      icon: Eye,
      href: '/vehicle/view',
      color: config?.theme.accent
    }
  ];

  return (
    <div className="space-y-8">
      {/* Welcome Section */}
      <div className="text-center">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">
          Welcome to Baskar Auto Consultancy
        </h1>
        <p className="text-gray-600 max-w-2xl mx-auto">
          Manage your vehicle records, track document validity, and stay compliant with all requirements.
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {stats.map((stat, index) => (
          <Card key={index} className="shadow-lg border-0 bg-white/70 backdrop-blur-sm hover:shadow-xl transition-all duration-300 hover:scale-105">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                  <p className="text-2xl font-bold text-gray-900 mt-1">{stat.title === 'Total Vehicles' ? metadata.totalVehicles : metadata.expiringSoon}</p>
                  {/* <p className="text-xs text-gray-500 mt-1">{stat.change}</p> */}
                </div>
                <div className={`w-12 h-12 rounded-xl ${stat.color} flex items-center justify-center`}>
                  <stat.icon className="w-6 h-6 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {quickActions.map((action, index) => (
          <Card key={index} className="shadow-lg border-0 bg-white/70 backdrop-blur-sm hover:shadow-xl transition-all duration-300 hover:scale-[1.02]">
            <CardHeader>
              <div className="flex items-center space-x-3">
                <div 
                  className="w-10 h-10 rounded-xl flex items-center justify-center"
                  style={{ backgroundColor: action.color }}
                >
                  <action.icon className="w-5 h-5 text-white" />
                </div>
                <div>
                  <CardTitle className="text-lg font-semibold">{action.title}</CardTitle>
                  <p className="text-sm text-gray-600 mt-1">{action.description}</p>
                </div>
              </div>
            </CardHeader>
            <CardContent className="pt-0">
              <Link to={action.href}>
                <Button 
                  className="w-full shadow-md hover:shadow-lg transition-all duration-200"
                  style={{ backgroundColor: action.color }}
                >
                  Get Started
                </Button>
              </Link>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Recent Activity */}
      <Card className="shadow-lg border-0 bg-white/70 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="text-xl font-semibold">Recent Activity</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {recentActivities
            .map((activity, index) => (
              <div key={index} className="flex items-center justify-between py-3 border-b border-gray-100 last:border-0">
                <div className="flex items-center space-x-3">
                  <div className={`w-2 h-2 rounded-full ${
                    activity.revisionType === 'ADD' ? 'bg-green-500' : activity.revisionType === 'MOD' ? 'bg-orange-500' : 'bg-red-500'
                  }`} />
                  <span className="text-gray-900">{activity.vehicleNumber} was {activity.revisionType === 'ADD' ? 'Added' : activity.revisionType === 'MOD' ? 'updated' : 'deleted'}</span>
                </div>
                <span className="text-sm text-gray-500">{getActivityTimeString(activity)}</span>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default Dashboard;
