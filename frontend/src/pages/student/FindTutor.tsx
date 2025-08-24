import Navbar from "@/components/Navbar";
import { useState } from "react";

const FindTutor = () => {
  const [search, setSearch] = useState("");
  const [subject, setSubject] = useState("");
  const [priceRange, setPriceRange] = useState("");
  const [rating, setRating] = useState("");
  const [availability, setAvailability] = useState("");

  const handleSearch = () => {
    // TODO: Call API with filters
    console.log({ search, subject, priceRange, rating, availability });
  };

  return (
    <div>
      <Navbar />
      <div className="min-h-screen bg-[#f2f2f2]">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-md p-12 flex flex-col md:flex-row items-center gap-6 min-h-[300px]">
          {/* Right: Text */}
          <div className="w-full md:w-3/5 flex flex-col justify-center">
            <h1 className="text-3xl md:text-4xl font-bold text-gray-800 mb-4">
              Find your tutor now
            </h1>
            <p className="text-gray-600 text-base md:text-lg">
              At TutorLink we offer personalized tutoring sessions to help you excel in your
              studies. Browse our selection of qualified tutors and find the perfect match for your
              learning style.
            </p>
          </div>

          {/* Left: Image */}
          <div className="w-full md:w-2/5 flex justify-center items-center">
            <img
              src="/src/assets/tutor.jpg" // replace with your image path
              alt="Tutor illustration"
              className="rounded-lg object-cover w-full h-full max-h-[300px]"
            />
          </div>
        </div>

        <div className="mx-auto p-6">
          {/* Search & Filters */}
          <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            <h2 className="text-xl font-bold mb-4">Find a Tutor</h2>

            <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
              {/* Search by name/keyword */}
              <input
                type="text"
                placeholder="Search by tutor name or keyword"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="col-span-2 border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
              />

              {/* Subject filter */}
              <select
                value={subject}
                onChange={(e) => setSubject(e.target.value)}
                className="border rounded-md px-3 py-2">
                <option value="">All Subjects</option>
                <option value="math">Math</option>
                <option value="science">Science</option>
                <option value="english">English</option>
                <option value="history">History</option>
              </select>

              {/* Price filter */}
              <select
                value={priceRange}
                onChange={(e) => setPriceRange(e.target.value)}
                className="border rounded-md px-3 py-2">
                <option value="">Any Price</option>
                <option value="0-20">$0 - $20/hr</option>
                <option value="20-50">$20 - $50/hr</option>
                <option value="50+">$50+/hr</option>
              </select>

              {/* Rating filter */}
              <select
                value={rating}
                onChange={(e) => setRating(e.target.value)}
                className="border rounded-md px-3 py-2">
                <option value="">Any Rating</option>
                <option value="4">&gt;= 4 Stars</option>
                <option value="3">&gt;= 3 Stars</option>
                <option value="2">&gt;= 2 Stars</option>
              </select>
            </div>

            {/* Availability filter */}
            <div className="mt-4 flex flex-col md:flex-row items-center gap-4">
              <label className="font-medium">Availability:</label>
              <select
                value={availability}
                onChange={(e) => setAvailability(e.target.value)}
                className="border rounded-md px-3 py-2">
                <option value="">Any Time</option>
                <option value="weekday">Weekdays</option>
                <option value="weekend">Weekends</option>
                <option value="evening">Evenings</option>
              </select>

              <button
                onClick={handleSearch}
                className="ml-auto bg-primary text-white px-6 py-2 rounded-md hover:bg-primary/80 transition">
                Search
              </button>
            </div>
          </div>

          {/* Tutor Results */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-white rounded-lg shadow-md p-5 flex flex-col justify-between h-full">
              <div>
                <h3 className="text-lg font-semibold">John Doe</h3>
                <p className="text-gray-600">Math Tutor</p>
                <p className="mt-2 text-sm">Rating: ⭐⭐⭐⭐ (4.5)</p>
                <p className="text-sm">Available: Weekdays, Evenings</p>
              </div>

              {/* Bottom row: Price + Button */}
              <div className="mt-4 flex items-center justify-between">
                <span className="text-xl font-bold text-primary">$30/hr</span>
                <button className="bg-primary text-white px-4 py-2 rounded-md hover:bg-primary/80 transition">
                  View Profile
                </button>
              </div>
            </div>

            {/* More cards will come from API */}
          </div>
        </div>
      </div>
    </div>
  );
};

export default FindTutor;
