use flights
  db.airplanes.insertMany([
    {
      characteristics: {
        max_speed: 100,
        max_acceleration: 10,
        height_change_speed: 5,
        course_change_speed: 30
      },
      flights: []
    },
    {
      characteristics: {
        max_speed: 87,
        max_acceleration: 8,
        height_change_speed: 6,
        course_change_speed: 45
      },
      flights: []
    },
    {
      characteristics: {
        max_speed: 134,
        max_acceleration: 12,
        height_change_speed: 7,
        course_change_speed: 20
      },
      flights: []
    }
  ]);