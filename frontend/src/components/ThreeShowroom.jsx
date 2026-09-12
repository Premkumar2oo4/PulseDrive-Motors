import { useEffect, useRef } from "react";
import { Canvas, useFrame, useThree } from "@react-three/fiber";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";

const paintColors = {
  black: "#12161b",
  white: "#e8edf1",
  silver: "#aeb8c2",
  grey: "#7f8b96",
  gray: "#7f8b96",
  red: "#b73535",
  blue: "#315d83",
  green: "#3e6b5c",
};

function getPaintColor(vehicle) {
  const rawColor = String(vehicle?.color || "silver").trim().toLowerCase();
  if (paintColors[rawColor]) {
    return paintColors[rawColor];
  }

  if (/^#(?:[0-9a-f]{3}|[0-9a-f]{6})$/i.test(rawColor)) {
    return rawColor;
  }

  return paintColors.silver;
}

function Wheel({ position }) {
  return (
    <mesh position={position} rotation={[Math.PI / 2, 0, 0]} castShadow>
      <cylinderGeometry args={[0.43, 0.43, 0.24, 32]} />
      <meshStandardMaterial color="#090b0d" roughness={0.32} metalness={0.65} />
      <mesh position={[0, 0.13, 0]}>
        <cylinderGeometry args={[0.2, 0.2, 0.025, 24]} />
        <meshStandardMaterial color="#c2ccd3" roughness={0.2} metalness={0.8} />
      </mesh>
    </mesh>
  );
}

function ShowroomCar({ vehicle }) {
  const carRef = useRef();
  const paint = getPaintColor(vehicle);

  useFrame((_, delta) => {
    if (carRef.current) carRef.current.rotation.y += delta * 0.12;
  });

  return (
    <group ref={carRef} scale={1.1} position={[0, -0.25, 0]}>
      <mesh castShadow>
        <boxGeometry args={[3.8, 0.65, 1.55]} />
        <meshPhysicalMaterial color={paint} clearcoat={1} clearcoatRoughness={0.12} metalness={0.75} roughness={0.2} />
      </mesh>
      <mesh position={[-0.15, 0.56, 0]} castShadow>
        <boxGeometry args={[2.25, 0.7, 1.35]} />
        <meshPhysicalMaterial color={paint} clearcoat={1} clearcoatRoughness={0.12} metalness={0.7} roughness={0.22} />
      </mesh>
      <mesh position={[-0.14, 0.9, 0]}>
        <boxGeometry args={[1.65, 0.38, 1.22]} />
        <meshPhysicalMaterial color="#17222a" transmission={0.12} transparent opacity={0.82} roughness={0.12} metalness={0.2} />
      </mesh>
      <mesh position={[1.62, 0.04, 0]} rotation={[0, Math.PI / 2, 0]}>
        <planeGeometry args={[0.44, 0.2]} />
        <meshStandardMaterial color="#d8efff" emissive="#a9ddff" emissiveIntensity={3} />
      </mesh>
      <mesh position={[-1.62, 0.04, 0]} rotation={[0, -Math.PI / 2, 0]}>
        <planeGeometry args={[0.44, 0.2]} />
        <meshStandardMaterial color="#ff4b3d" emissive="#ff271b" emissiveIntensity={2} />
      </mesh>
      <Wheel position={[-1.18, -0.4, 0.82]} />
      <Wheel position={[1.18, -0.4, 0.82]} />
      <Wheel position={[-1.18, -0.4, -0.82]} />
      <Wheel position={[1.18, -0.4, -0.82]} />
    </group>
  );
}

function SceneControls() {
  const controlsRef = useRef();
  const { camera, gl } = useThree();

  useEffect(() => {
    const controls = new OrbitControls(camera, gl.domElement);
    controls.enablePan = false;
    controls.minDistance = 4.2;
    controls.maxDistance = 8;
    controls.minPolarAngle = Math.PI / 3.2;
    controls.maxPolarAngle = Math.PI / 2.05;
    controlsRef.current = controls;
    return () => controls?.dispose();
  }, [camera, gl]);

  useFrame(() => controlsRef.current?.update());

  return null;
}

function ThreeShowroom({ vehicle }) {
  return (
    <div className="showroom-canvas" aria-label="Interactive 3D vehicle showroom">
      <Canvas camera={{ position: [4.8, 2.8, 5.8], fov: 38 }} shadows="basic" dpr={[1, 2]}>
        <color attach="background" args={["#101820"]} />
        <fog attach="fog" args={["#101820", 7, 15]} />
        <ambientLight intensity={0.8} />
        <spotLight position={[4, 7, 4]} angle={0.35} penumbra={1} intensity={75} castShadow />
        <pointLight position={[-4, 1, -2]} color="#5db7ff" intensity={12} />
        <ShowroomCar vehicle={vehicle} />
        <SceneControls />
        <mesh rotation={[-Math.PI / 2, 0, 0]} position={[0, -0.92, 0]} receiveShadow>
          <circleGeometry args={[5.5, 64]} />
          <meshStandardMaterial color="#18242d" roughness={0.74} metalness={0.35} />
        </mesh>
      </Canvas>
      <span className="showroom-hint">Drag to rotate / scroll to zoom</span>
    </div>
  );
}

export default ThreeShowroom;
