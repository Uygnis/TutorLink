import { PieChart, Pie, Cell, ResponsiveContainer } from "recharts";

const COLORS = ["#48ea83ff", "#fa5d5dff", "#7ed5d5ff", "#ee904eff"]; 

interface RingChartProps {
  title: string;
  total: number;
  active: number;
  suspended: number;
  pending? :number;
  rejected?: number; // optional since not all categories have it
  
}

const RingChart = ({ title, total, active, suspended, pending = 0, rejected = 0 }: RingChartProps) => {
  const data = [
    { name: "Active", value: active },
    { name: "Suspended", value: suspended },
    { name: "Pending", value: pending},
    { name: "Rejected", value: rejected },
  ];

  const percentage = total ? ((active / total) * 100).toFixed(1) : "0";

  return (
    <div className="flex flex-col items-center justify-center bg-white rounded-md shadow p-4">
      <h3 className="font-semibold text-gray-700 text-sm mb-2">{title}</h3>
      <div className="relative w-32 h-32">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={data}
              innerRadius={40}
              outerRadius={60}
              dataKey="value"
              startAngle={90}
              endAngle={450}
              stroke="none"
            >
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index]} />
              ))}
            </Pie>
          </PieChart>
        </ResponsiveContainer>

        <div className="absolute inset-0 flex items-center justify-center text-xl font-bold text-gray-700">
          {percentage}%
        </div>
      </div>

      <div className="text-xs text-gray-500 mt-2 space-y-1 text-center">
        <p>{active} active</p>
        <p>{suspended} suspended</p>
        {pending > 0 && <p>{pending} pending</p>}
        {rejected > 0 && <p>{rejected} rejected</p>}
      </div>
    </div>
  );
};

export default RingChart;
