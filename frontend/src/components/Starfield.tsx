import { useMemo } from 'react';

const generateShadows = (n: number) => {
  let value = `${Math.floor(Math.random() * 2000)}px ${Math.floor(Math.random() * 2000)}px #FFF`;
  for (let i = 2; i <= n; i++) {
    value += `, ${Math.floor(Math.random() * 2000)}px ${Math.floor(Math.random() * 2000)}px #FFF`;
  }
  return value;
};

export default function Starfield() {
  // Generate the star coordinates once on mount
  const shadowsSmall = useMemo(() => generateShadows(700), []);
  const shadowsMedium = useMemo(() => generateShadows(200), []);
  const shadowsLarge = useMemo(() => generateShadows(100), []);

  return (
    <div style={{ position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', zIndex: -1, background: 'radial-gradient(ellipse at bottom, #1b2735 0%, #090a0f 100%)', overflow: 'hidden' }}>
      <style>
        {`
          @keyframes animStar {
            from { transform: translateY(0px); }
            to { transform: translateY(-2000px); }
          }
          .star-layer {
            position: absolute;
            top: 0;
            left: 0;
            background: transparent;
          }
          /* This creates the seamless infinite loop by duplicating the stars exactly 2000px below */
          .star-layer::after {
            content: " ";
            position: absolute;
            top: 2000px;
            width: inherit;
            height: inherit;
            background: transparent;
            box-shadow: inherit;
          }
        `}
      </style>
      
      {/* 3 Parallax layers with different speeds and star sizes */}
      <div className="star-layer" style={{ width: '1px', height: '1px', boxShadow: shadowsSmall, animation: 'animStar 50s linear infinite' }} />
      <div className="star-layer" style={{ width: '2px', height: '2px', boxShadow: shadowsMedium, animation: 'animStar 100s linear infinite' }} />
      <div className="star-layer" style={{ width: '3px', height: '3px', boxShadow: shadowsLarge, animation: 'animStar 150s linear infinite' }} />
    </div>
  );
}
