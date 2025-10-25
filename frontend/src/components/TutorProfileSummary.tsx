import AvailabilityCalendar from "@/components/AvailabilityCalendar";

const ProfileSummary = ({
    profile,
    bookedSlots = [],
    initialMonth = new Date(),
}: {
    profile: any;
    bookedSlots?: { date: string; status: string }[];
    initialMonth?: Date;
}) => {
    if (!profile) return <p className="text-gray-500">No profile data.</p>;
    console.log("Profile Data:", profile);
    return (
        <div className="space-y-4">
            <div className="grid grid-cols-2 md:grid-cols-2">
                <div className="space-x-4 pr-6">
                    {/* Basic Info */}
                    <p>
                        <span className="font-semibold">Subjects:</span> {profile.subject}
                    </p>
                    <p>
                        <span className="font-semibold">Hourly Rate:</span> SGD {profile.hourlyRate || "??"}/hr
                    </p>
                    <p>
                        <div>
                            <span className="font-semibold">Lesson Types:</span>
                            {profile.lessonType && profile.lessonType.length > 0 ? (
                                <ul className="list-disc list-inside ml-5 mt-1">
                                    {profile.lessonType.map((type: string, index: number) => (
                                        <li key={index}>{type}</li>
                                    ))}
                                </ul>
                            ) : (
                                <span> N/A</span>
                            )}
                        </div>
                    </p>
                </div>

                {/* Qualifications */}
                <div className="grid grid-cols-1 gap-4">
                    <div className="bg-white rounded-lg shadow-md p-6 max-h-[320px] overflow-y-auto">
                        <h2 className="text-xl font-semibold mb-3">Qualifications</h2>
                        {profile.qualifications && profile.qualifications.length > 0 ? (
                            <ul className="space-y-3">
                                {profile.qualifications.map((q: any, idx: number) => (
                                    <li key={idx} className="border rounded-lg p-3 flex justify-between items-center">
                                        <div>
                                            <p className="font-semibold">{q.name}</p>
                                            <p className="text-gray-500 text-sm">{q.type}</p>
                                            {q.uploadedAt && (
                                                <p className="text-xs text-gray-400">
                                                    Uploaded: {new Date(q.uploadedAt).toLocaleDateString()}
                                                </p>
                                            )}
                                        </div>
                                        <a
                                            href={q.path}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="text-blue-600 hover:underline text-sm">
                                            View
                                        </a>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p className="text-gray-500">No qualifications uploaded.</p>
                        )}
                    </div>
                </div>
            </div>
            <p>
                        <span className="font-semibold">Description:</span> {profile.description || "No Description has been provided."}
                    </p>

            {/* Availability Calendar */}
            <div className="mt-4">
                <p className="font-semibold mb-1">Availability:</p>
                <AvailabilityCalendar
                    role="student"
                    availability={profile.availability}
                    bookedSlots={bookedSlots}
                    initialMonth={initialMonth}
                    onMonthChange={() => { }}
                />

            </div>
        </div>
    );
};

export default ProfileSummary;
